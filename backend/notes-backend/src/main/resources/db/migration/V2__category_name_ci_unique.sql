ALTER TABLE public.categories
DROP CONSTRAINT IF EXISTS categories_name_key;

-- Índice único funcional sobre LOWER(name)
CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_name_ci
    ON public.categories (LOWER(name));
