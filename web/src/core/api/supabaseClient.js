import { createClient } from '@supabase/supabase-js'

const SUPABASE_URL = 'https://eppmobyckusswlunzxdq.supabase.co'
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVwcG1vYnlja3Vzc3dsdW56eGRxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMwMDY4MTgsImV4cCI6MjA4ODU4MjgxOH0.lRWT0rXR5B32FmchVKN5j8Rcr27lIzFCBKKTCgP1xYE' // Replace with your actual anon key

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY)
