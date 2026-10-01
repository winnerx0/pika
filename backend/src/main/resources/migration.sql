alter table vector_store add column search_vector tsvector generated always as (to_tsvector('english', coalesce(content, ''))) stored;

create index vectore_store_search_idx on vector_store using gin(search_vector)