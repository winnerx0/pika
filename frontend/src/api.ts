type ApiOptions = Omit<RequestInit, 'headers'> & {
  token?: string
  headers?: HeadersInit
}

type StreamOptions = {
  token?: string
  onChunk?: (chunk: string) => void
}

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

export async function api<T>(path: string, { token, ...options }: ApiOptions = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
  if (token) headers.set('Authorization', `Bearer ${token}`)

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  const payload: unknown = response.status === 204 ? null : await response.json().catch(() => null)
  if (!response.ok) {
    const message = typeof payload === 'object' && payload !== null && 'message' in payload
      ? String(payload.message)
      : typeof payload === 'object' && payload !== null && 'error' in payload
        ? String(payload.error)
        : `Request failed (${response.status})`
    throw new Error(message)
  }
  return payload as T
}

export async function subscribeToSession(
  sessionId: string,
  { token, onChunk }: StreamOptions,
): Promise<() => void> {
  const controller = new AbortController()
  const response = await fetch(`${API_BASE_URL}/messages/sse/${sessionId}`, {
    headers: {
      Accept: 'text/event-stream',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    signal: controller.signal,
  })

  if (!response.ok) throw new Error(`Request failed (${response.status})`)
  if (!response.body) throw new Error('Streaming is not available in this browser.')

  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''
  let eventLines: string[] = []

  function consumeEvent() {
    const chunk = eventLines
      .filter(line => line.startsWith('data:'))
      .map(line => line.slice(5).replace(/^ /, ''))
      .join('\n')
    eventLines = []
    if (chunk && chunk !== '[DONE]') onChunk?.(chunk)
  }

  void (async () => {
    while (true) {
      const { value, done } = await reader.read()
      buffer += decoder.decode(value || new Uint8Array(), { stream: !done })
      const lines = buffer.split(/\r?\n/)
      buffer = lines.pop() || ''
      lines.forEach(line => {
        if (line === '') consumeEvent()
        else eventLines.push(line)
      })
      if (done) {
        if (buffer) eventLines.push(buffer)
        consumeEvent()
        break
      }
    }
  })().catch(error => {
    if (!controller.signal.aborted) console.error('SSE connection failed', error)
  })

  return () => controller.abort()
}
