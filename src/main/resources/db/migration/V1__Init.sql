CREATE TYPE user_role_enum AS ENUM ('STUDENT', 'TEACHER', 'MANAGER', 'ADMIN');
CREATE TYPE subject_enum AS ENUM ('MATH', 'PHYSICS', 'CHEMISTRY', 'ENGLISH', 'RUSSIAN', 'CHINESE');
CREATE TYPE member_status_enum AS ENUM ('PENDING', 'APPROVED');
CREATE TYPE question_type_enum AS ENUM ('ESSAY', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE');
CREATE TYPE target_type_enum AS ENUM ('TOPIC', 'COMMENT');
CREATE TYPE reaction_type_enum AS ENUM ('LIKE', 'LOVE', 'HAHA', 'WOW', 'SAD', 'ANGRY');
CREATE TYPE conversation_type_enum AS ENUM ('PRIVATE', 'GROUP');


CREATE TABLE user_humg (
                           id SERIAL PRIMARY KEY,
                           username VARCHAR(50) UNIQUE NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL,
                           password_hash VARCHAR(255),
                           google_id VARCHAR(255) UNIQUE,
                           school_mail VARCHAR(100) UNIQUE,
                           full_name VARCHAR(100) NOT NULL,
                           avatar_url VARCHAR(255),
                           role user_role_enum NOT NULL DEFAULT 'STUDENT',
                           is_active BOOLEAN DEFAULT TRUE,
                           created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE team (
                      id SERIAL PRIMARY KEY,
                      creator_id INT NOT NULL,
                      name VARCHAR(100) NOT NULL,
                      subject subject_enum NOT NULL,
                      description TEXT,
                      badge_icon_url VARCHAR(255),
                      created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (creator_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE team_member (
                             team_id INT NOT NULL,
                             user_id INT NOT NULL,
                             is_leader BOOLEAN DEFAULT FALSE,
                             status member_status_enum DEFAULT 'PENDING',
                             joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (team_id, user_id),
                             FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE,
                             FOREIGN KEY (user_id) REFERENCES user_humg(id) ON DELETE CASCADE
);

CREATE TABLE document (
                          id SERIAL PRIMARY KEY,
                          uploader_id INT NOT NULL,
                          title VARCHAR(200) NOT NULL,
                          file_url VARCHAR(255) NOT NULL,
                          file_type VARCHAR(50),
                          description TEXT,
                          is_public BOOLEAN DEFAULT TRUE,
                          created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (uploader_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE contest (
                         id SERIAL PRIMARY KEY,
                         creator_id INT NOT NULL,
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         start_time TIMESTAMP,
                         end_time TIMESTAMP,
                         created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (creator_id) REFERENCES user_humg (id) ON DELETE SET NULL
);

CREATE TABLE contest_participant (
                                     contest_id INT NOT NULL,
                                     user_id INT NOT NULL,
                                     joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (contest_id, user_id),
                                     FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE,
                                     FOREIGN KEY (user_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE exercise (
                          id SERIAL PRIMARY KEY,
                          creator_id INT NOT NULL,
                          contest_id INT,
                          title VARCHAR(200) NOT NULL,
                          description TEXT,
                          start_time TIMESTAMP,
                          end_time TIMESTAMP,
                          created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (creator_id) REFERENCES user_humg (id) ON DELETE CASCADE,
                          FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE SET NULL
);

CREATE TABLE question (
                          id SERIAL PRIMARY KEY,
                          exercise_id INT NOT NULL,
                          content TEXT NOT NULL,
                          question_type question_type_enum NOT NULL,
                          points FLOAT DEFAULT 1.0,
                          created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE
);

CREATE TABLE question_option (
                                 id SERIAL PRIMARY KEY,
                                 question_id INT NOT NULL,
                                 content VARCHAR(255) NOT NULL,
                                 is_correct BOOLEAN DEFAULT FALSE,
                                 FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);

CREATE TABLE submission (
                            id SERIAL PRIMARY KEY,
                            user_id INT NOT NULL,
                            exercise_id INT NOT NULL,
                            score FLOAT,
                            start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            submit TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES user_humg (id) ON DELETE CASCADE,
                            FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE
);

CREATE TABLE submission_answer (
                                   id SERIAL PRIMARY KEY,
                                   submission_id INT NOT NULL,
                                   question_id INT NOT NULL,
                                   essay_answer TEXT,
                                   selected_option_id INT,
                                   FOREIGN KEY (submission_id) REFERENCES submission(id) ON DELETE CASCADE,
                                   FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);

CREATE TABLE forum_categories (
                                 id SERIAL PRIMARY KEY,
                                 name VARCHAR(100) NOT NULL,
                                 description TEXT
);

CREATE TABLE forum_topic (
                             id SERIAL PRIMARY KEY,
                             category_id INT NOT NULL,
                             author_id INT NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             content TEXT NOT NULL,
                             is_pinned BOOLEAN DEFAULT FALSE,
                             view_count INT DEFAULT 0,
                             created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (category_id) REFERENCES forum_categories(id) ON DELETE CASCADE,
                             FOREIGN KEY (author_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE forum_comment (
                               id SERIAL PRIMARY KEY,
                               topic_id INT NOT NULL,
                               author_id INT NOT NULL,
                               parent_comment_id INT,
                               content TEXT NOT NULL,
                               created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (topic_id) REFERENCES forum_topic(id) ON DELETE CASCADE,
                               FOREIGN KEY (author_id) REFERENCES user_humg (id) ON DELETE CASCADE,
                               FOREIGN KEY (parent_comment_id) REFERENCES forum_comment(id) ON DELETE CASCADE
);

CREATE TABLE reaction (
                          id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL,
                          target_type target_type_enum NOT NULL,
                          target_id INT NOT NULL,
                          reaction_type reaction_type_enum NOT NULL,
                          created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE (user_id, target_type, target_id),
                          FOREIGN KEY (user_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE conversation (
                              id SERIAL PRIMARY KEY,
                              type conversation_type_enum NOT NULL,
                              name VARCHAR(100),
                              created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE conversation_participant (
                                          conversation_id INT NOT NULL,
                                          user_id INT NOT NULL,
                                          joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          PRIMARY KEY (conversation_id, user_id),
                                          FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE,
                                          FOREIGN KEY (user_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE message (
                         id SERIAL PRIMARY KEY,
                         conversation_id INT NOT NULL,
                         sender_id INT NOT NULL,
                         content TEXT NOT NULL,
                         created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE,
                         FOREIGN KEY (sender_id) REFERENCES user_humg (id) ON DELETE CASCADE
);

CREATE TABLE chatbot_log (
                             id SERIAL PRIMARY KEY,
                             user_id INT NOT NULL,
                             user_message TEXT NOT NULL,
                             bot_response TEXT NOT NULL,
                             created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES user_humg (id) ON DELETE CASCADE
);


