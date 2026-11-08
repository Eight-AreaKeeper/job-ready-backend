-- job_ready test data. Run AFTER db/schema.sql:
--   mysql -uroot -p job_ready_db < db/test-data.sql      (or open it in Navicat and run it)
-- Safe to run again: it first deletes only the test rows it created (ids containing "_t_").
-- Every test account's password is: password123
-- All companies and postings are fictional.

USE job_ready_db;
SET NAMES utf8mb4;

DELETE FROM coffee_chats   WHERE id LIKE 'c\_t\_%';
DELETE FROM schedule_items WHERE id LIKE 's\_t\_%';
DELETE FROM mentors        WHERE id LIKE 'm\_t\_%';
DELETE FROM profiles       WHERE id LIKE 'p\_t\_%';
DELETE FROM resumes        WHERE opportunity_id LIKE 'o\_t\_%';
DELETE FROM opportunities  WHERE id LIKE 'o\_t\_%';
DELETE FROM users          WHERE id LIKE 'u\_t\_%';

-- 6 students (u_t_s01..s06; s06 has no profile, to test the questionnaire) and 4 mentors
INSERT INTO users (id, name, email, password_hash, role) VALUES
  ('u_t_s01', 'Maya Chen', 'maya.chen@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'student'),
  ('u_t_s02', 'Liam Patel', 'liam.patel@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'student'),
  ('u_t_s03', 'Sofia Rossi', 'sofia.rossi@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'student'),
  ('u_t_s04', 'Noah Kim', 'noah.kim@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'student'),
  ('u_t_s05', 'Aisha Bello', 'aisha.bello@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'student'),
  ('u_t_s06', 'Ethan Wong', 'ethan.wong@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'student'),
  ('u_t_m01', 'Alex Morgan', 'alex.morgan@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'mentor'),
  ('u_t_m02', 'Priya Shah', 'priya.shah@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'mentor'),
  ('u_t_m03', 'Daniel Lee', 'daniel.lee@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'mentor'),
  ('u_t_m04', 'Grace Okafor', 'grace.okafor@test.jobready.dev', '$2a$10$u9M4yEh4hLeNzhLPfgG/OO9tg1FEAeFOCOqiM3xx/knkKK7Dp/L5i', 'mentor');

INSERT INTO profiles (id, user_id, summary, skills, education, projects, interests, experience, industries, preferred_work_type, experience_level, location, updated_at) VALUES
  ('p_t_s01', 'u_t_s01', 'Third-year Computer Science student focused on backend development and databases.', '["Java", "Spring Boot", "MySQL", "Git", "REST APIs"]', '[{"school": "University of Calgary", "degree": "BSc Computer Science", "end": "2027"}]', '[{"name": "Campus Marketplace", "bullets": ["Built a Spring Boot REST API used by 300+ students", "Designed the MySQL schema and indexes"]}]', '["backend", "cloud"]', '[{"title": "IT Support Assistant", "org": "University of Calgary", "start": "2025-05", "end": "2025-08", "bullets": ["Resolved 200+ help-desk tickets", "Wrote a Python script that automated laptop setup"]}]', '["Software", "Energy"]', 'Startup', 'student', 'Calgary, AB', '2026-11-07 12:00:00'),
  ('p_t_s02', 'u_t_s02', 'Data Science student who enjoys turning messy data into dashboards.', '["Python", "pandas", "SQL", "Tableau", "Excel"]', '[{"school": "University of Alberta", "degree": "BSc Statistics", "end": "2026"}]', '[{"name": "Transit Delay Dashboard", "bullets": ["Analysed 1M rows of open transit data", "Published a Tableau dashboard of delay hot spots"]}]', '["data", "public policy"]', '[]', '["Health", "Government"]', 'Large company', 'student', 'Edmonton, AB', '2026-11-07 12:00:00'),
  ('p_t_s03', 'u_t_s03', 'Mechanical Engineering student moving into renewable energy and simulation.', '["MATLAB", "Python", "SolidWorks", "CFD"]', '[{"school": "University of Calgary", "degree": "BSc Mechanical Engineering", "end": "2027"}]', '[{"name": "Small Wind Turbine Blade", "bullets": ["Simulated blade designs in CFD", "Improved efficiency by 8% over the baseline"]}]', '["renewables", "sustainability"]', '[{"title": "Engineering Intern", "org": "Prairie Wind Co. (fictional)", "start": "2025-05", "end": "2025-08", "bullets": ["Collected and cleaned turbine sensor data"]}]', '["Energy"]', 'Research lab', 'intermediate', 'Calgary, AB', '2026-11-07 12:00:00'),
  ('p_t_s04', 'u_t_s04', 'First-year student exploring front-end development and UX.', '["HTML", "CSS", "JavaScript", "Figma"]', '[{"school": "SAIT", "degree": "Diploma, Software Development", "end": "2028"}]', '[{"name": "Personal Portfolio", "bullets": ["Built a responsive portfolio site with vanilla JavaScript"]}]', '["design", "web"]', '[]', '["Software"]', 'Startup', 'beginner', 'Calgary, AB', '2026-11-07 12:00:00'),
  ('p_t_s05', 'u_t_s05', 'Biomedical student interested in machine learning for health care.', '["Python", "PyTorch", "scikit-learn", "R"]', '[{"school": "University of Toronto", "degree": "BSc Biomedical Engineering", "end": "2026"}]', '[{"name": "Skin Lesion Classifier", "bullets": ["Trained a CNN on 10k labelled images", "Reached 87% validation accuracy"]}]', '["AI", "health"]', '[{"title": "Research Volunteer", "org": "Hospital research lab (fictional)", "start": "2024-09", "end": "2025-04", "bullets": ["Labelled imaging data and ran baseline models"]}]', '["Health", "Research"]', 'Research lab', 'intermediate', 'Toronto, ON', '2026-11-07 12:00:00');

-- 20 opportunities across job / internship / research and local / remote / abroad
INSERT INTO opportunities (id, title, organization, type, industry, location, scope, description, requirements, deadline, url) VALUES
  ('o_t_001', 'Backend Developer Intern', 'Northwind Software (fictional)', 'internship', 'Software', 'Calgary, AB', 'local', 'TEST DATA. Build REST APIs in Java and Spring Boot for a logistics platform.', '["Java", "Spring Boot", "SQL"]', '2026-12-01', 'https://example.com/test-jobs/1'),
  ('o_t_002', 'Junior Data Engineer', 'Bow River Analytics (fictional)', 'job', 'Software', 'Remote (Canada)', 'remote', 'TEST DATA. Maintain data pipelines and SQL models for client dashboards.', '["Python", "SQL", "Airflow"]', '2026-12-10', 'https://example.com/test-jobs/2'),
  ('o_t_003', 'Front-End Developer Co-op', 'Chinook Apps (fictional)', 'internship', 'Software', 'Calgary, AB', 'local', 'TEST DATA. Build React components and improve accessibility.', '["JavaScript", "React", "CSS"]', '2026-11-28', 'https://example.com/test-jobs/3'),
  ('o_t_004', 'Reservoir Data Analyst Intern', 'Foothills Energy (fictional)', 'internship', 'Energy', 'Calgary, AB', 'local', 'TEST DATA. Analyse production data and automate reports.', '["Python", "pandas", "Excel"]', '2026-12-05', 'https://example.com/test-jobs/4'),
  ('o_t_005', 'Renewable Energy Research Assistant', 'Prairie Clean Energy Lab (fictional)', 'research', 'Energy', 'Calgary, AB', 'local', 'TEST DATA. Model wind and solar output with simulation tools.', '["MATLAB", "Python", "CFD"]', '2026-12-20', 'https://example.com/test-jobs/5'),
  ('o_t_006', 'Clinical Data Analyst', 'Maple Health Network (fictional)', 'job', 'Health', 'Remote (Canada)', 'remote', 'TEST DATA. Build dashboards on patient-flow data.', '["SQL", "Tableau", "Excel"]', '2026-12-15', 'https://example.com/test-jobs/6'),
  ('o_t_007', 'Machine Learning Research Intern', 'North Medical AI Lab (fictional)', 'research', 'Health', 'Toronto, ON', 'local', 'TEST DATA. Train image models for diagnostic support.', '["Python", "PyTorch", "scikit-learn"]', '2027-01-10', 'https://example.com/test-jobs/7'),
  ('o_t_008', 'Policy Data Analyst (Student)', 'Province of Alberta (fictional posting)', 'job', 'Government', 'Edmonton, AB', 'local', 'TEST DATA. Support policy teams with open-data analysis.', '["SQL", "Python", "Excel"]', '2026-12-08', 'https://example.com/test-jobs/8'),
  ('o_t_009', 'Cloud Support Intern', 'Aurora Cloud (fictional)', 'internship', 'Software', 'Remote (Canada)', 'remote', 'TEST DATA. Help customers troubleshoot Linux and cloud deployments.', '["Linux", "AWS", "Git"]', '2026-12-12', 'https://example.com/test-jobs/9'),
  ('o_t_010', 'Software Engineer Intern, Berlin', 'Spree Tech GmbH (fictional)', 'internship', 'Software', 'Berlin, Germany', 'abroad', 'TEST DATA. Work on a payments backend in Java.', '["Java", "Spring Boot", "Docker"]', '2027-01-15', 'https://example.com/test-jobs/10'),
  ('o_t_011', 'Data Science Intern, London', 'Thames Analytics (fictional)', 'internship', 'Finance', 'London, UK', 'abroad', 'TEST DATA. Forecast demand with Python models.', '["Python", "pandas", "scikit-learn"]', '2027-01-20', 'https://example.com/test-jobs/11'),
  ('o_t_012', 'Product Design Intern', 'Lakeview Studio (fictional)', 'internship', 'Software', 'Vancouver, BC', 'local', 'TEST DATA. Design and test app flows with users.', '["Figma", "UX research", "HTML"]', '2026-12-18', 'https://example.com/test-jobs/12'),
  ('o_t_013', 'Junior Full-Stack Developer', 'Cedar Labs (fictional)', 'job', 'Software', 'Remote (Canada)', 'remote', 'TEST DATA. Ship features across a Spring Boot and React app.', '["Java", "React", "MySQL"]', '2027-01-05', 'https://example.com/test-jobs/13'),
  ('o_t_014', 'Grid Modernization Co-op', 'Alberta Grid Services (fictional)', 'internship', 'Energy', 'Edmonton, AB', 'local', 'TEST DATA. Analyse grid sensor data and build alerts.', '["Python", "SQL", "Power BI"]', '2026-12-03', 'https://example.com/test-jobs/14'),
  ('o_t_015', 'Health Informatics Research Assistant', 'Rocky Mountain Health Research (fictional)', 'research', 'Health', 'Calgary, AB', 'local', 'TEST DATA. Clean and link health records for studies.', '["R", "SQL", "Statistics"]', '2026-12-22', 'https://example.com/test-jobs/15'),
  ('o_t_016', 'QA Automation Intern', 'Elbow Software (fictional)', 'internship', 'Software', 'Calgary, AB', 'local', 'TEST DATA. Write automated tests for web and APIs.', '["Java", "Selenium", "Git"]', '2026-11-25', 'https://example.com/test-jobs/16'),
  ('o_t_017', 'Junior Financial Analyst', 'Stampede Capital (fictional)', 'job', 'Finance', 'Calgary, AB', 'local', 'TEST DATA. Build financial models and reports.', '["Excel", "SQL", "Python"]', '2026-12-30', 'https://example.com/test-jobs/17'),
  ('o_t_018', 'Remote Python Developer (Part-time)', 'Open Source Collective (fictional)', 'job', 'Software', 'Remote (Worldwide)', 'remote', 'TEST DATA. Maintain Python libraries and review contributions.', '["Python", "Git", "Testing"]', NULL, 'https://example.com/test-jobs/18'),
  ('o_t_019', 'AI Research Intern, Singapore', 'Lion City AI (fictional)', 'research', 'Research', 'Singapore', 'abroad', 'TEST DATA. Explore large language model evaluation.', '["Python", "PyTorch", "NLP"]', '2027-02-01', 'https://example.com/test-jobs/19'),
  ('o_t_020', 'Digital Services Developer (Student)', 'City of Calgary (fictional posting)', 'job', 'Government', 'Calgary, AB', 'local', 'TEST DATA. Build small web tools for city services.', '["JavaScript", "SQL", "Accessibility"]', '2026-12-14', 'https://example.com/test-jobs/20');

INSERT INTO mentors (id, user_id, headline, company, industries, expertise, availability, max_chats_per_week, points) VALUES
  ('m_t_01', 'u_t_m01', 'Senior Backend Engineer', 'Northwind Software (fictional)', '["Software"]', '["Java", "System design", "Interview prep"]', '[{"day": "Tue", "start": "18:00", "end": "19:00"}, {"day": "Thu", "start": "18:00", "end": "19:00"}]', 2, 30),
  ('m_t_02', 'u_t_m02', 'Data Science Manager', 'Maple Health Network (fictional)', '["Health", "Software"]', '["Data science", "Career switching", "Resume review"]', '[{"day": "Wed", "start": "12:00", "end": "13:00"}]', 1, 50),
  ('m_t_03', 'u_t_m03', 'Reservoir Engineer turned Data Engineer', 'Foothills Energy (fictional)', '["Energy", "Software"]', '["Data pipelines", "Energy industry", "Career switching"]', '[{"day": "Mon", "start": "17:30", "end": "18:30"}]', 2, 10),
  ('m_t_04', 'u_t_m04', 'UX Lead', 'Lakeview Studio (fictional)', '["Software"]', '["UX design", "Portfolios", "Networking"]', '[{"day": "Fri", "start": "16:00", "end": "17:00"}]', 3, 0);

INSERT INTO schedule_items (id, user_id, opportunity_id, status) VALUES
  ('s_t_01', 'u_t_s01', 'o_t_001', 'applied'),
  ('s_t_02', 'u_t_s01', 'o_t_013', 'saved'),
  ('s_t_03', 'u_t_s01', 'o_t_010', 'interview'),
  ('s_t_04', 'u_t_s02', 'o_t_006', 'applied'),
  ('s_t_05', 'u_t_s02', 'o_t_008', 'saved'),
  ('s_t_06', 'u_t_s03', 'o_t_005', 'offer'),
  ('s_t_07', 'u_t_s05', 'o_t_007', 'saved');

-- scheduled_at is UTC
INSERT INTO coffee_chats (id, student_id, mentor_id, status, message, proposed_times, scheduled_at) VALUES
  ('c_t_01', 'u_t_s01', 'm_t_01', 'pending', 'Hi Alex, I am building Spring Boot APIs and would love your advice on backend internships.', '["2026-11-12T18:00:00Z", "2026-11-14T18:00:00Z"]', NULL),
  ('c_t_02', 'u_t_s02', 'm_t_02', 'accepted', 'Hi Priya, could we chat about moving from statistics into health data science?', '["2026-11-13T19:00:00Z"]', '2026-11-13 19:00:00'),
  ('c_t_03', 'u_t_s03', 'm_t_03', 'completed', 'Hi Daniel, I would like to hear how you moved from engineering to data.', '["2026-11-03T00:30:00Z"]', '2026-11-03 00:30:00'),
  ('c_t_04', 'u_t_s04', 'm_t_04', 'declined', 'Hi Grace, could you review my portfolio?', '["2026-11-08T23:00:00Z"]', NULL);
