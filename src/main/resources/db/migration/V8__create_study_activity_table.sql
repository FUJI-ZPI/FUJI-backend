CREATE TABLE IF NOT EXISTS public.study_activity (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    card_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    drawing_data JSONB NOT NULL,
    strokes_accuracy JSONB NOT NULL,
    overall_accuracy DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_study_activity_card FOREIGN KEY (card_id) REFERENCES public.cards (id)
);

CREATE INDEX IF NOT EXISTS idx_study_activity_card_id ON public.study_activity (card_id);
CREATE INDEX IF NOT EXISTS idx_study_activity_timestamp ON public.study_activity (timestamp);