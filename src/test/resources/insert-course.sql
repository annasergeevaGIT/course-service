INSERT INTO courses(name, description, price, category, duration, difficulty, image_url, module_collection, created_at)
VALUES
    ('Java Basics', 'Fundamentals of Java', 100, 'ENGINEERING', 1000, 'BEGINNER',
    'http://images.com/java-basics.png',
    '{"modules":[{"name":"Introduction","duration":100},{"name":"OOP Concepts","duration":100},{"name":"Collections","duration":90}]}',
    '2025-02-18 10:28:54'),

    ('Business Strategy Basics', 'Learn how to design and execute successful business strategies', 79.99, 'BUSINESS', 1000, 'INTERMEDIATE',
     'http://images.com/business-strategy.png',
     '{"modules":[{"name":"Market Analysis","duration":60},{"name":"Competitive Advantage","duration":120},{"name":"Implementation","duration":90}]}',
     '2025-02-18 10:23:54'),

    ('Python for Developers', 'Master Python programming', 79, 'ENGINEERING', 1400, 'INTERMEDIATE',
     'http://images.com/python-developers.png',
     '{"modules":[{"name":"Python Basics","duration":90},{"name":"Data Structures","duration":120},{"name":"OOP with Python","duration":150}]}',
     '2025-02-18 10:29:54'),

    ('Web Development with JavaScript', 'Learn how to build interactive web applications', 60, 'ENGINEERING', 1600, 'ADVANCED',
     'http://images.com/web-development-js.png',
     '{"modules":[{"name":"HTML & CSS","duration":120},{"name":"JavaScript Fundamentals","duration":180},{"name":"Frontend Projects","duration":240}]}',
     '2025-02-18 10:30:54'),

    ('Physics Made Simple', 'Explore the fundamentals of classical mechanics', 69, 'SCIENCE', 1200, 'INTERMEDIATE',
     'http://images.com/physics.png',
     '{"modules":[{"name":"Mechanics","duration":180},{"name":"Energy","duration":120},{"name":"Heat","duration":150}]}',
     '2025-02-18 10:26:54'),

    ('Modern Art Techniques', 'Learn modern art', 54, 'ARTS', 900, 'ADVANCED',
     'http://images.com/modern-art.png',
     '{"modules":[{"name":"Abstract Art","duration":90},{"name":"Expressionism","duration":120},{"name":"Mixed Media","duration":180}]}',
     '2025-02-18 10:27:54');
