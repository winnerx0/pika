import { useEffect, useMemo, useRef, useState, type FormEvent, type ReactNode } from 'react'
import { api, subscribeToSession } from './api'

type IconName = 'plus' | 'message' | 'send' | 'logout' | 'menu' | 'spark' | 'chevron' | 'close' | 'sun'
type StoredUser = { name: string; email: string; accessToken: string; refreshToken: string }
type ChatMessage = { id: string; content: string; role: 'user' | 'assistant'; sessionId?: string }
type ChatSession = { id: string; title: string; messages?: ChatMessage[] }
type ApiResponse<T> = { message: string; data: T }
type AuthResponse = { message: string; accessToken: string; refreshToken: string }
type MessageResponse = { id: string; content: string; sessionId: string; role?: 'USER' | 'ASSISTANT' }

function errorMessage(error: unknown, fallback: string) {
  return error instanceof Error ? error.message : fallback
}

const starterPrompts = [
  { icon: '✳', text: 'What does the Act say about tax payment deadlines?' },
  { icon: '↗', text: 'Explain tax deductions at source' },
  { icon: '⌘', text: 'Which provision covers employee income tax?' },
]

function Icon({ name, size = 18 }: { name: IconName; size?: number }) {
  const common = { width: size, height: size, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true as const }
  const paths: Record<IconName, ReactNode> = {
    plus: <><path d="M12 5v14M5 12h14" /></>,
    message: <><path d="M21 11.5a8.4 8.4 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.4 8.4 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.4 8.4 0 0 1 3.8-.9h.5a8.5 8.5 0 0 1 8 8z" /></>,
    send: <><path d="m22 2-7 20-4-9-9-4Z" /><path d="M22 2 11 13" /></>,
    logout: <><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" /><path d="m16 17 5-5-5-5M21 12H9" /></>,
    menu: <><path d="M4 6h16M4 12h16M4 18h16" /></>,
    spark: <><path d="m12 3 1.9 5.8L20 11l-6.1 2.2L12 19l-1.9-5.8L4 11l6.1-2.2L12 3Z" /><path d="m19 14 1.2 2.3L22.5 17l-2.3.8L19 20l-.8-2.2L16 17l2.2-.7L19 14Z" /></>,
    chevron: <><path d="m9 18 6-6-6-6" /></>,
    close: <><path d="m18 6-12 12M6 6l12 12" /></>,
    sun: <><circle cx="12" cy="12" r="4" /><path d="M12 2v2m0 16v2M4.93 4.93l1.42 1.42m11.3 11.3 1.42 1.42M2 12h2m16 0h2M4.93 19.07l1.42-1.42m11.3-11.3 1.42-1.42" /></>,
  }
  return <svg {...common}>{paths[name]}</svg>
}

function getStoredUser(): StoredUser | null {
  try {
    const value: unknown = JSON.parse(localStorage.getItem('pika-user') || 'null')
    if (typeof value !== 'object' || value === null) return null
    if (!('name' in value) || !('email' in value) || !('accessToken' in value) || !('refreshToken' in value)) return null
    return {
      name: String(value.name),
      email: String(value.email),
      accessToken: String(value.accessToken),
      refreshToken: String(value.refreshToken),
    }
  } catch { return null }
}

function AuthScreen({ onAuthenticated }: { onAuthenticated: (user: StoredUser) => void }) {
  const [isRegister, setIsRegister] = useState(false)
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const result = await api<AuthResponse>(`/auth/${isRegister ? 'register' : 'login'}`, {
        method: 'POST',
        body: JSON.stringify(isRegister
          ? { username: form.username, email: form.email, password: form.password }
          : { email: form.email, password: form.password }),
      })
      const user = { name: isRegister ? form.username : form.email.split('@')[0], email: form.email, accessToken: result.accessToken, refreshToken: result.refreshToken }
      localStorage.setItem('pika-user', JSON.stringify(user))
      onAuthenticated(user)
    } catch (e) {
      setError(errorMessage(e, 'Something went wrong. Please try again.'))
    } finally { setLoading(false) }
  }

  return (
    <main className="auth-shell">
      <section className="auth-story">
        <div className="brand brand-on-dark"><span className="brand-mark"><Icon name="spark" size={19} /></span><span>pika</span></div>
        <div className="story-copy">
          <span className="eyebrow"><span className="status-dot" /> NIGERIAN TAX ACT AI</span>
          <h1>Understand the<br />Tax Act with <em>clarity.</em></h1>
          <p>Ask questions about Nigerian tax law and get answers grounded in the indexed Act.</p>
          <div className="story-note"><div className="note-icon"><Icon name="spark" size={18} /></div><div><strong>Grounded in the Act.</strong><span>Relevant provisions, explained clearly.</span></div><span className="note-arrow">↗</span></div>
        </div>
        <div className="story-footer"><span>Nigerian tax law, made easier to understand.</span><span>© 2026 Pika</span></div>
        <div className="orb orb-one" /><div className="orb orb-two" />
      </section>
      <section className="auth-panel">
        <div className="auth-top"><span>Already have an account?</span><button className="text-button" onClick={() => { setIsRegister(false); setError('') }}>{isRegister ? 'Log in' : 'Get started'}</button></div>
        <div className="auth-card">
          <div className="auth-mobile-brand brand"><span className="brand-mark"><Icon name="spark" size={19} /></span><span>pika</span></div>
          <span className="eyebrow">{isRegister ? 'YOUR NEXT CHAPTER' : 'WELCOME BACK'}</span>
          <h2>{isRegister ? 'Create your account' : 'Good to see you'}</h2>
          <p className="auth-description">{isRegister ? 'Create an account to explore the Nigerian Tax Act with Pika.' : 'Sign in to continue asking questions about the Nigerian Tax Act.'}</p>
          <form onSubmit={submit} className="auth-form">
            {isRegister && <label>Your name<input autoComplete="name" value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} placeholder="How should we call you?" required minLength={2} /></label>}
            <label>Email address<input type="email" autoComplete="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" required /></label>
            <label>Password<input type="password" autoComplete={isRegister ? 'new-password' : 'current-password'} value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="At least 8 characters" required minLength={8} /></label>
            {error && <div className="form-error" role="alert">{error}</div>}
            <button className="primary-button auth-submit" type="submit" disabled={loading}>{loading ? 'One moment…' : isRegister ? 'Create account' : 'Log in'}<Icon name="chevron" size={16} /></button>
          </form>
          <div className="auth-switch">{isRegister ? 'Already part of Pika?' : 'New to Pika?'} <button className="inline-button" onClick={() => { setIsRegister(!isRegister); setError('') }}>{isRegister ? 'Log in' : 'Create an account'}</button></div>
          <div className="auth-privacy"><span className="privacy-lock">✳</span> A quieter corner of the internet, just for you.</div>
        </div>
      </section>
    </main>
  )
}

function App() {
  const [user, setUser] = useState<StoredUser | null>(getStoredUser)
  const [sessions, setSessions] = useState<ChatSession[]>([])
  const [activeId, setActiveId] = useState<string | null>(null)
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [draft, setDraft] = useState('')
  const [loading, setLoading] = useState(false)
  const [thinking, setThinking] = useState(false)
  const [error, setError] = useState('')
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const inputRef = useRef<HTMLTextAreaElement>(null)
  const bottomRef = useRef<HTMLDivElement>(null)
  const streamCloseRef = useRef<(() => void) | null>(null)
  const initials = useMemo(() => (user?.name || 'P').slice(0, 1).toUpperCase(), [user])
  const activeSession = sessions.find(session => session.id === activeId)

  useEffect(() => {
    if (!user) return
    setLoading(true)
    api<ApiResponse<ChatSession[]>>('/sessions', { token: user.accessToken })
      .then(result => { const items = result.data || []; setSessions(items); if (items.length) setActiveId(items[0].id) })
      .catch(e => setError(errorMessage(e, 'Could not load conversations.')))
      .finally(() => setLoading(false))
  }, [user])

  useEffect(() => {
    if (!user || !activeId) { setMessages([]); return }
    api<ApiResponse<MessageResponse[]>>(`/messages/session/${activeId}`, { token: user.accessToken })
      .then(result => setMessages((result.data || []).map(item => ({ ...item, role: item.role?.toLowerCase() === 'assistant' ? 'assistant' : 'user' }))))
      .catch(e => setError(errorMessage(e, 'Could not load messages.')))
  }, [user, activeId])

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' }) }, [messages, thinking])

  useEffect(() => () => streamCloseRef.current?.(), [])

  function signOut() {
    streamCloseRef.current?.()
    streamCloseRef.current = null
    localStorage.removeItem('pika-user')
    setUser(null); setSessions([]); setMessages([]); setActiveId(null); setError('')
  }

  async function newChat() {
    if (!user) return
    setError('')
    try {
      const response = await api<ApiResponse<ChatSession>>('/sessions', { token: user.accessToken, method: 'POST', body: JSON.stringify({ title: 'New conversation' }) })
      const session = response.data
      setSessions(previous => [session, ...previous])
      setActiveId(session.id)
      setMessages([])
      setSidebarOpen(false)
      setTimeout(() => inputRef.current?.focus(), 50)
    } catch (e) { setError(errorMessage(e, 'Could not create a conversation.')) }
  }

  async function sendMessage(value = draft) {
    const content = value.trim()
    if (!user || !content || thinking) return
    setError('')
    let sessionId = activeId
    const shouldSetTitle = !activeSession || activeSession.title === 'New conversation'
    if (!sessionId) {
      try {
        const response = await api<ApiResponse<ChatSession>>('/sessions', { token: user.accessToken, method: 'POST', body: JSON.stringify({ title: content.slice(0, 48) }) })
        sessionId = response.data.id
        setSessions(previous => [response.data, ...previous])
        setActiveId(sessionId)
      } catch (e) { setError(errorMessage(e, 'Could not create a conversation.')); return }
    }
    setDraft('')
    const temporaryUserId = `temp-user-${Date.now()}`
    const temporaryAssistantId = `temp-assistant-${Date.now()}`
    setMessages(previous => [...previous, { id: temporaryUserId, content, role: 'user' }])
    setThinking(true)
    try {
      streamCloseRef.current?.()
      streamCloseRef.current = await subscribeToSession(sessionId, {
        token: user.accessToken,
        onChunk: chunk => {
          setThinking(false)
          setMessages(previous => {
            const assistantMessage = previous.find(message => message.id === temporaryAssistantId)
            if (assistantMessage) return previous.map(message => message.id === temporaryAssistantId ? { ...message, content: message.content + chunk } : message)
            return [...previous, { id: temporaryAssistantId, content: chunk, role: 'assistant' }]
          })
        },
      })
      await api<ApiResponse<string>>(`/messages/session/${sessionId}`, {
        token: user.accessToken,
        method: 'POST',
        headers: { Accept: 'application/json' },
        body: JSON.stringify({ content, role: 'USER' }),
      })
      const saved = await api<ApiResponse<MessageResponse[]>>(`/messages/session/${sessionId}`, { token: user.accessToken })
      setMessages((saved.data || []).map(item => ({ ...item, role: item.role?.toLowerCase() === 'assistant' ? 'assistant' : 'user' })))
      if (shouldSetTitle) {
        const title = content.length > 48 ? `${content.slice(0, 45)}…` : content
        await api<ApiResponse<ChatSession>>(`/sessions/${sessionId}`, { token: user.accessToken, method: 'PUT', body: JSON.stringify({ title }) })
        setSessions(previous => previous.map(session => session.id === sessionId ? { ...session, title } : session))
      }
    } catch (e) {
      setError(errorMessage(e, 'I couldn’t reach Pika just now. Please try again.'))
      setMessages(previous => previous.filter(message => message.id !== temporaryAssistantId))
    } finally { setThinking(false) }
  }

  if (!user) return <AuthScreen onAuthenticated={setUser} />

  return (
    <main className="app-shell">
      {sidebarOpen && <button className="sidebar-scrim" aria-label="Close navigation" onClick={() => setSidebarOpen(false)} />}
      <aside className={`sidebar ${sidebarOpen ? 'sidebar-open' : ''}`}>
        <div className="sidebar-brand-row"><a className="brand" href="#"><span className="brand-mark"><Icon name="spark" size={18} /></span><span>pika</span></a><button className="icon-button sidebar-close" aria-label="Close sidebar" onClick={() => setSidebarOpen(false)}><Icon name="close" /></button></div>
        <button className="new-chat-button" onClick={newChat}><Icon name="plus" size={18} /><span>New conversation</span><kbd>⌘ K</kbd></button>
        <div className="nav-label">YOUR SPACE</div>
        <div className="history-list">
          {loading && <div className="history-empty">Loading your conversations…</div>}
          {!loading && sessions.length === 0 && <div className="history-empty">Your conversations will show up here.</div>}
          {sessions.map(session => <button key={session.id} className={`history-item ${session.id === activeId ? 'history-active' : ''}`} onClick={() => { setActiveId(session.id); setSidebarOpen(false) }}><Icon name="message" size={16} /><span>{session.title || 'New conversation'}</span></button>)}
        </div>
        <div className="sidebar-bottom"><div className="plan-card"><div className="plan-spark"><Icon name="spark" size={16} /></div><div><strong>Ask the Nigerian Tax Act.</strong><span>Explore its provisions in plain language.</span></div><Icon name="chevron" size={16} /></div>
          <div className="profile-row"><div className="avatar">{initials}</div><div className="profile-info"><strong>{user.name}</strong><span>{user.email}</span></div><button className="icon-button logout-button" onClick={signOut} aria-label="Log out" title="Log out"><Icon name="logout" size={17} /></button></div>
        </div>
      </aside>
      <section className="workspace">
        <header className="topbar"><button className="icon-button mobile-menu" aria-label="Open navigation" onClick={() => setSidebarOpen(true)}><Icon name="menu" /></button><div className="breadcrumb"><span>Nigerian Tax Act</span><Icon name="chevron" size={14} /><strong>{activeSession?.title || 'New question'}</strong></div><div className="topbar-right"><span className="online-indicator"><i /> Tax Act AI</span><button className="icon-button topbar-profile" aria-label="Account" onClick={signOut}><div className="avatar avatar-small">{initials}</div></button></div></header>
        <div className={`chat-area ${messages.length ? 'chat-has-messages' : ''}`}>
          {messages.length === 0 && !thinking ? <div className="welcome-content"><div className="welcome-icon"><Icon name="spark" size={23} /></div><span className="eyebrow">NIGERIAN TAX ACT ASSISTANT</span><h1>What does the Act<br />say about <em>your question?</em></h1><p>Ask about a tax provision, process, or obligation.</p><div className="suggestion-grid">{starterPrompts.map(prompt => <button className="suggestion-card" key={prompt.text} onClick={() => sendMessage(prompt.text)}><span className="suggestion-icon">{prompt.icon}</span><span>{prompt.text}</span><Icon name="chevron" size={15} /></button>)}</div></div> : <div className="message-thread">{messages.map(message => <article className={`message-row ${message.role === 'user' ? 'message-user' : 'message-assistant'}`} key={message.id}><div className="message-avatar">{message.role === 'user' ? initials : <Icon name="spark" size={15} />}</div><div className="message-body"><span className="message-author">{message.role === 'user' ? 'You' : 'Pika'}</span><p>{message.content}</p></div></article>)}{thinking && <article className="message-row message-assistant"><div className="message-avatar"><Icon name="spark" size={15} /></div><div className="message-body"><span className="message-author">Pika</span><div className="typing-indicator"><i /><i /><i /></div></div></article>}<div ref={bottomRef} /></div>}
          {error && <div className="inline-error" role="status"><span>{error}</span><button className="icon-button" aria-label="Dismiss error" onClick={() => setError('')}><Icon name="close" size={15} /></button></div>}
        </div>
        <div className="composer-wrap"><form className="composer" onSubmit={event => { event.preventDefault(); sendMessage() }}><textarea ref={inputRef} value={draft} onChange={event => setDraft(event.target.value)} onKeyDown={event => { if (event.key === 'Enter' && !event.shiftKey) { event.preventDefault(); sendMessage() } }} placeholder="Ask about the Nigerian Tax Act…" rows={1} aria-label="Ask about the Nigerian Tax Act"/><div className="composer-footer"><span><Icon name="sun" size={14} /> Answers grounded in indexed tax law</span><button className="send-button" type="submit" disabled={!draft.trim() || thinking} aria-label="Send message"><Icon name="send" size={17} /></button></div></form><div className="composer-caption">Check the cited provisions against the source Act.</div></div>
      </section>
    </main>
  )
}

export default App
