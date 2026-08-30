-- =============================================================
-- Downtime Service - Sample Data for a Bottling Plant (dev)
-- Insert order: downtime_category -> downtime_reason -> downtime_events
-- production_unit_id and process_order_id reference the shared 'dev' DB
-- =============================================================

-- ─────────────────────────────────────────────────────────────
-- 1. DOWNTIME CATEGORY
--    isPlanned: 1 = Planned, 0 = Unplanned
-- ─────────────────────────────────────────────────────────────
INSERT INTO downtime_category (category_id, category_name, is_planned) VALUES
(1, 'Mechanical',           0),
(2, 'Electrical',           0),
(3, 'Process / Quality',    0),
(4, 'No Material',          0),
(5, 'Planned Maintenance',  1),
(6, 'Changeover',           1),
(7, 'CIP / Sanitation',     1);

-- ─────────────────────────────────────────────────────────────
-- 2. DOWNTIME REASON
-- ─────────────────────────────────────────────────────────────
INSERT INTO downtime_reason (downtime_reason_id, reason_code, reason_description, category_id) VALUES
-- Mechanical
(1,  'BOTTLE_JAM',      'Bottle jam at filler inlet',          1),
(2,  'CONVEYOR_STOP',   'Conveyor belt stopped / snapped',      1),
(3,  'PUMP_FAILURE',    'Syrup pump failure',                   1),
(4,  'CAP_MISFEED',     'Cap misfeed at capping station',       1),
(5,  'SEAL_LEAK',       'Seal / gasket leak detected',          1),
-- Electrical
(6,  'POWER_TRIP',      'Power trip / electrical fault',        2),
(7,  'SENSOR_FAULT',    'Sensor malfunction or miscalibration', 2),
(8,  'MOTOR_BURNOUT',   'Drive motor burnout',                  2),
-- Process / Quality
(9,  'FILL_OOS',        'Fill level out of specification',      3),
(10, 'FOAMING',         'Excessive foaming in filler',          3),
(11, 'LABEL_SKIP',      'Label misalignment / skipped',         3),
-- No Material
(12, 'NO_BOTTLES',      'Bottle supply depleted',               4),
(13, 'NO_CAPS',         'Cap supply depleted',                  4),
(14, 'NO_SYRUP',        'Syrup / concentrate run out',          4),
-- Planned Maintenance
(15, 'SCHED_PM',        'Scheduled preventive maintenance',     5),
(16, 'LUBRICATION',     'Lubrication / greasing round',         5),
-- Changeover
(17, 'SKU_CHANGE',      'SKU changeover (product switch)',       6),
(18, 'FORMAT_CHANGE',   'Format changeover (bottle size)',       6),
-- CIP / Sanitation
(19, 'CIP_CYCLE',       'Clean-In-Place (CIP) cycle',           7),
(20, 'SANITIZATION',    'Manual sanitation / hygiene clean',    7);

-- ─────────────────────────────────────────────────────────────
-- 3. DOWNTIME EVENTS
--    production_unit_id  → references production_unit.id (mes-service)
--    process_order_id    → references process_order.id   (process-order-service)
--    endTime NULL        → event is still OPEN (machine currently down)
--    durationMinutes     → stored for efficient filtering/sorting
--    isPlanned mirrors the reason's category (denormalised for query speed)
-- ─────────────────────────────────────────────────────────────
INSERT INTO downtime_events
    (production_unit_id, process_order_id, start_time, end_time, duration_minutes, downtime_reason_id, is_planned, comments, created_at)
VALUES
-- Unit 1 (Filler #1, Line 1) – Bottle jam, resolved
(1, 1, '2026-08-28 06:15:00', '2026-08-28 06:43:00',  28.0,  1,  0, 'Bottles bridged at inlet guide. Cleared manually.',      '2026-08-28 06:15:00'),

-- Unit 1 (Filler #1, Line 1) – CIP cycle (planned)
(1, 1, '2026-08-28 12:00:00', '2026-08-28 13:30:00',  90.0, 19,  1, 'Mid-shift CIP cycle as per schedule.',                   '2026-08-28 12:00:00'),

-- Unit 2 (Capper #1, Line 1) – Cap misfeed, resolved
(2, 1, '2026-08-28 07:50:00', '2026-08-28 08:10:00',  20.0,  4,  0, 'Cap worm gear slipped. Re-aligned and tested.',          '2026-08-28 07:50:00'),

-- Unit 3 (Labeler #1, Line 1) – Label skip, resolved
(3, 2, '2026-08-29 09:05:00', '2026-08-29 09:35:00',  30.0, 11,  0, 'Label roll tension lost. Re-threaded and calibrated.',   '2026-08-29 09:05:00'),

-- Unit 4 (Filler #2, Line 2) – SKU changeover (planned)
(4, 3, '2026-08-29 14:00:00', '2026-08-29 14:45:00',  45.0, 17,  1, 'Changeover from 500ml to 1.5L format.',                  '2026-08-29 14:00:00'),

-- Unit 4 (Filler #2, Line 2) – No syrup after changeover
(4, 3, '2026-08-29 15:10:00', '2026-08-29 15:40:00',  30.0, 14,  0, 'Syrup tank ran dry. Refill took 30 min.',                '2026-08-29 15:10:00'),

-- Unit 5 (Conveyor, Line 2) – Conveyor belt snapped, long repair
(5, 3, '2026-08-29 16:00:00', '2026-08-29 17:45:00', 105.0,  2,  0, 'Conveyor belt snapped near station 3. Replaced belt.',   '2026-08-29 16:00:00'),

-- Unit 2 (Capper #1, Line 1) – Power trip
(2, NULL, '2026-08-30 05:30:00', '2026-08-30 05:55:00', 25.0, 6,  0, 'Power trip before shift start. Reset MCB panel.',       '2026-08-30 05:30:00'),

-- Unit 1 (Filler #1, Line 1) – Scheduled PM (planned)
(1, NULL, '2026-08-30 06:00:00', '2026-08-30 07:00:00', 60.0, 15,  1, 'Weekly PM: seal inspection and bearing check.',         '2026-08-30 06:00:00'),

-- Unit 3 (Labeler #1, Line 1) – OPEN event (machine currently down)
(3, 4, '2026-08-30 08:45:00', NULL, NULL, 1, 0, 'Bottle jam ongoing at filler inlet. Technician on site.', '2026-08-30 08:45:00');
