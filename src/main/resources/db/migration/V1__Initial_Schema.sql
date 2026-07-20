--
-- PostgreSQL database dump
--


-- Dumped from database version 15.18
-- Dumped by pg_dump version 15.18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: authors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authors (
    id uuid NOT NULL,
    bio text,
    main_title character varying(255),
    preferred_social_network character varying(255),
    preferred_social_network_username character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone,
    name character varying(255) NOT NULL,
    email character varying(255) NOT NULL
);

--
-- Name: bibliographic_references; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bibliographic_references (
    id uuid NOT NULL,
    post_id uuid NOT NULL,
    description text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone
);

--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    slug character varying(255),
    description text,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone
);

--
-- Name: flyway_test; Type: TABLE; Schema: public; Owner: postgres
--

--
-- Name: flyway_test_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

--
-- Name: flyway_test_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

--
-- Name: footnotes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.footnotes (
    id uuid NOT NULL,
    post_id uuid NOT NULL,
    description text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone
);

--
-- Name: password_reset_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.password_reset_tokens (
    id uuid NOT NULL,
    token character varying(255) NOT NULL,
    user_id uuid NOT NULL,
    expiry_date timestamp without time zone NOT NULL,
    used boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by character varying(255) DEFAULT 'system'::character varying,
    updated_by character varying(255),
    deleted_at timestamp without time zone
);

--
-- Name: posts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.posts (
    id uuid NOT NULL,
    title character varying(255) NOT NULL,
    slug character varying(255) NOT NULL,
    content text,
    status character varying(50) NOT NULL,
    author_id uuid NOT NULL,
    category_id uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone,
    image_path character varying(255),
    tldr text,
    published_at timestamp without time zone,
    format_type character varying(20) DEFAULT 'HTML'::character varying NOT NULL,
    search_vector tsvector GENERATED ALWAYS AS (((setweight(to_tsvector('portuguese'::regconfig, (COALESCE(title, ''::character varying))::text), 'A'::"char") || setweight(to_tsvector('portuguese'::regconfig, COALESCE(tldr, ''::text)), 'B'::"char")) || setweight(to_tsvector('portuguese'::regconfig, COALESCE(content, ''::text)), 'C'::"char"))) STORED
);

--
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.refresh_tokens (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token character varying(255) NOT NULL,
    expiry_date timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone
);

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    first_name character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    created_by character varying(255) NOT NULL,
    updated_by character varying(255),
    deleted_at timestamp without time zone,
    avatar_path character varying(255),
    last_name character varying(255),
    role character varying(50) NOT NULL
);

--
-- Name: flyway_test id; Type: DEFAULT; Schema: public; Owner: postgres
--

--
-- Name: authors authors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authors
    ADD CONSTRAINT authors_pkey PRIMARY KEY (id);

--
-- Name: bibliographic_references bibliographic_references_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bibliographic_references
    ADD CONSTRAINT bibliographic_references_pkey PRIMARY KEY (id);

--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);

--
-- Name: flyway_test flyway_test_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

--
-- Name: footnotes footnotes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.footnotes
    ADD CONSTRAINT footnotes_pkey PRIMARY KEY (id);

--
-- Name: password_reset_tokens password_reset_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id);

--
-- Name: password_reset_tokens password_reset_tokens_token_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_token_key UNIQUE (token);

--
-- Name: posts posts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT posts_pkey PRIMARY KEY (id);

--
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);

--
-- Name: refresh_tokens refresh_tokens_token_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_key UNIQUE (token);

--
-- Name: refresh_tokens refresh_tokens_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_user_id_key UNIQUE (user_id);

--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);

--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

--
-- Name: categories_name_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX categories_name_idx ON public.categories USING btree (name) WHERE (deleted_at IS NULL);

--
-- Name: categories_slug_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX categories_slug_idx ON public.categories USING btree (slug) WHERE (deleted_at IS NULL);

--
-- Name: idx_posts_search_vector; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_posts_search_vector ON public.posts USING gin (search_vector);

--
-- Name: posts_slug_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX posts_slug_idx ON public.posts USING btree (slug) WHERE (deleted_at IS NULL);

--
-- Name: bibliographic_references fk_biblio_post; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bibliographic_references
    ADD CONSTRAINT fk_biblio_post FOREIGN KEY (post_id) REFERENCES public.posts(id);

--
-- Name: footnotes fk_footnote_post; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.footnotes
    ADD CONSTRAINT fk_footnote_post FOREIGN KEY (post_id) REFERENCES public.posts(id);

--
-- Name: password_reset_tokens fk_password_reset_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;

--
-- Name: posts fk_post_author; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES public.authors(id);

--
-- Name: posts fk_post_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.posts
    ADD CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE RESTRICT;

--
-- Name: refresh_tokens fk_user_refresh_token; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT fk_user_refresh_token FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;

--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

--
-- PostgreSQL database dump complete
--

