-- Reset database completely
DROP DATABASE IF EXISTS movie_review_system;
-- Create database
CREATE DATABASE IF NOT EXISTS movie_review_system;
USE movie_review_system;

-- Genres table
CREATE TABLE IF NOT EXISTS genres (
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Movies table
CREATE TABLE IF NOT EXISTS movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    release_year INT,
    genre_id INT,
    director VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id),
    INDEX idx_title (title),
    INDEX idx_genre (genre_id)
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);

-- Ratings table
CREATE TABLE IF NOT EXISTS ratings (
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    score INT NOT NULL CHECK (score >= 1 AND score <= 5),
    review_text TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_movie (user_id, movie_id),
    INDEX idx_movie_rating (movie_id),
    INDEX idx_user_rating (user_id),
    INDEX idx_timestamp (timestamp)
);

-- Add genres (use INSERT IGNORE to skip duplicates if re-running)
INSERT IGNORE INTO genres (name) VALUES 
('Action'), ('Comedy'), ('Drama'), ('Horror'), ('Sci-Fi'), 
('Romance'), ('Thriller'), ('Documentary'), ('Animation'), 
('Adventure'), ('Fantasy'), ('Mystery'), ('Crime'), ('Family'),
('Musical'), ('War'), ('Western'), ('Biography'), ('History'),
('Sport'), ('Superhero'), ('Noir'), ('Psychological Thriller'),
-- Indian cinema specific genres
('Masala'), ('Devotional'), ('Social'), ('Period Drama'), 
('Romantic Comedy'), ('Action Thriller'), ('Comedy Drama'),
('Epic'), ('Mythology'), ('Patriotic');

-- Create sample users with proper BCrypt hashes (password: 'password123')
INSERT IGNORE INTO users (username, email, password_hash) VALUES
('john_doe', 'john@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('jane_smith', 'jane@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('movie_buff', 'buff@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('cinema_lover', 'cinema@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('film_critic', 'critic@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('raj_kumar', 'raj@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('priya_sharma', 'priya@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('amit_patel', 'amit@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('neha_gupta', 'neha@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ=='),
('rahul_verma', 'rahul@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjQ==');

-- Hollywood Movies
INSERT IGNORE INTO movies (title, release_year, genre_id, director, description) VALUES
-- Action Movies
('Die Hard', 1988, 1, 'John McTiernan', 'NYPD cop battles terrorists in LA skyscraper'),
('Mad Max: Fury Road', 2015, 1, 'George Miller', 'Post-apocalyptic action chase'),
('John Wick', 2014, 1, 'Chad Stahelski', 'Retired hitman seeks vengeance'),
('The Terminator', 1984, 5, 'James Cameron', 'Cyborg assassin from the future'),
('Top Gun', 1986, 1, 'Tony Scott', 'Elite fighter pilot training'),
('Mission: Impossible', 1996, 1, 'Brian De Palma', 'IMF agent framed for murder'),
('The Bourne Identity', 2002, 1, 'Doug Liman', 'Amnesiac discovers he is a trained assassin'),
('Gladiator', 2000, 1, 'Ridley Scott', 'Roman general becomes gladiator for revenge'),
('300', 2006, 1, 'Zack Snyder', 'Spartans battle Persian army'),
('Raiders of the Lost Ark', 1981, 10, 'Steven Spielberg', 'Archaeologist searches for biblical artifact'),

-- Sci-Fi Movies
('Star Wars', 1977, 5, 'George Lucas', 'Farm boy becomes galactic hero'),
('Blade Runner', 1982, 5, 'Ridley Scott', 'Cop hunts replicants in dystopian future'),
('E.T.', 1982, 5, 'Steven Spielberg', 'Boy befriends alien'),
('Back to the Future', 1985, 5, 'Robert Zemeckis', 'Teen travels through time'),
('Alien', 1979, 5, 'Ridley Scott', 'Crew encounters deadly alien creature'),
('The Matrix Reloaded', 2003, 5, 'Wachowski Brothers', 'Neo continues fight against machines'),
('Avatar', 2009, 5, 'James Cameron', 'Marine on alien planet'),
('Arrival', 2016, 5, 'Denis Villeneuve', 'Linguist communicates with aliens'),
('Ex Machina', 2014, 5, 'Alex Garland', 'Programmer tests artificial intelligence'),

-- Drama Movies
('The Pursuit of Happyness', 2006, 3, 'Gabriele Muccino', 'Homeless salesman becomes stockbroker'),
('12 Years a Slave', 2013, 3, 'Steve McQueen', 'Free man kidnapped into slavery'),
('A Beautiful Mind', 2001, 3, 'Ron Howard', 'Mathematician battles schizophrenia'),
('The Social Network', 2010, 3, 'David Fincher', 'Creation of Facebook'),
('Whiplash', 2014, 3, 'Damien Chazelle', 'Drummer and abusive instructor'),
('The Pianist', 2002, 3, 'Roman Polanski', 'Jewish pianist survives Holocaust'),
('Good Will Hunting', 1997, 3, 'Gus Van Sant', 'Janitor is mathematical genius'),
('Rain Man', 1988, 3, 'Barry Levinson', 'Man discovers autistic brother'),
('The Green Mile', 1999, 3, 'Frank Darabont', 'Death row guards discover miracle'),
('Schindlers List', 1993, 3, 'Steven Spielberg', 'Businessman saves Jews during Holocaust'),

-- Comedy Movies
('The Hangover', 2009, 2, 'Todd Phillips', 'Friends piece together wild night in Vegas'),
('Superbad', 2007, 2, 'Greg Mottola', 'Teenagers try to obtain alcohol'),
('Anchorman', 2004, 2, 'Adam McKay', '1970s news anchor rivalry'),
('Borat', 2006, 2, 'Larry Charles', 'Kazakh journalist tours America'),
('Ghostbusters', 1984, 2, 'Ivan Reitman', 'Scientists start ghost removal service'),
('Home Alone', 1990, 2, 'Chris Columbus', 'Boy defends home from burglars'),
('Mrs. Doubtfire', 1993, 2, 'Chris Columbus', 'Divorced dad poses as nanny'),
('The Big Lebowski', 1998, 2, 'Coen Brothers', 'Slacker mistaken for millionaire'),
('Dumb and Dumber', 1994, 2, 'Farrelly Brothers', 'Two friends on cross-country trip'),
('Meet the Parents', 2000, 2, 'Jay Roach', 'Man meets girlfriend parents'),

-- BOLLYWOOD MOVIES
-- Action/Masala (using Masala genre_id = 24)
('Sholay', 1975, 24, 'Ramesh Sippy', 'Two criminals hired to capture dacoit Gabbar Singh'),
('Dhoom', 2004, 1, 'Sanjay Gadhvi', 'Cop chases motorcycle gang'),
('War', 2019, 1, 'Siddharth Anand', 'Student turns against mentor spy'),
('Baahubali', 2015, 31, 'S.S. Rajamouli', 'Epic tale of two brothers and a kingdom'),
('KGF', 2018, 1, 'Prashanth Neel', 'Man rises in gold mafia underworld'),
('Singham', 2011, 1, 'Rohit Shetty', 'Honest cop fights corruption'),
('Dabangg', 2010, 1, 'Abhinav Kashyap', 'Fearless cop Chulbul Pandey'),
('Don', 2006, 1, 'Farhan Akhtar', 'Common man impersonates crime boss'),
('Ghajini', 2008, 1, 'A.R. Murugadoss', 'Man with memory loss seeks revenge'),
('Bang Bang', 2014, 1, 'Siddharth Anand', 'Bank receptionist meets mysterious thief'),

-- Romance
('Dilwale Dulhania Le Jayenge', 1995, 6, 'Aditya Chopra', 'NRI wins over traditional father'),
('Kuch Kuch Hota Hai', 1998, 6, 'Karan Johar', 'Love triangle across two timelines'),
('Veer-Zaara', 2004, 6, 'Yash Chopra', 'Cross-border love story'),
('Jab We Met', 2007, 6, 'Imtiaz Ali', 'Depressed businessman meets bubbly girl'),
('Kal Ho Naa Ho', 2003, 6, 'Nikhil Advani', 'Dying man helps friend find love'),
('Kabir Singh', 2019, 6, 'Sandeep Reddy Vanga', 'Surgeon spirals after breakup'),
('Yeh Jawaani Hai Deewani', 2013, 6, 'Ayan Mukerji', 'Friends reunite at wedding'),
('Aashiqui 2', 2013, 6, 'Mohit Suri', 'Singer mentors and loves upcoming artist'),
('Barfi', 2012, 6, 'Anurag Basu', 'Deaf-mute man and autistic girl love story'),
('2 States', 2014, 6, 'Abhishek Varman', 'Couple from different states convince parents'),

-- Drama
('Dangal', 2016, 3, 'Nitesh Tiwari', 'Wrestler trains daughters for Commonwealth'),
('3 Idiots', 2009, 3, 'Rajkumar Hirani', 'Engineering students challenge education system'),
('Taare Zameen Par', 2007, 3, 'Aamir Khan', 'Teacher helps dyslexic child'),
('Rang De Basanti', 2006, 3, 'Rakeysh Omprakash Mehra', 'Youth awakened by freedom fighter story'),
('Lagaan', 2001, 27, 'Ashutosh Gowariker', 'Villagers play cricket to avoid tax'),
('Swades', 2004, 3, 'Ashutosh Gowariker', 'NASA scientist returns to Indian village'),
('Chak De India', 2007, 20, 'Shimit Amin', 'Disgraced player coaches women hockey team'),
('Black', 2005, 3, 'Sanjay Leela Bhansali', 'Deaf-blind girl and her teacher'),
('Queen', 2013, 3, 'Vikas Bahl', 'Jilted bride goes on honeymoon alone'),
('Pink', 2016, 3, 'Aniruddha Roy Chowdhury', 'Lawyer defends women in consent case'),

-- Comedy
('Hera Pheri', 2000, 2, 'Priyadarshan', 'Three men try get-rich-quick schemes'),
('Munna Bhai MBBS', 2003, 2, 'Rajkumar Hirani', 'Gangster pretends to be doctor'),
('Andaz Apna Apna', 1994, 2, 'Rajkumar Santoshi', 'Two slackers compete for heiress'),
('Golmaal', 2006, 2, 'Rohit Shetty', 'Four friends create chaos with lies'),
('Welcome', 2007, 2, 'Anees Bazmee', 'Man unknowingly engaged to don sister'),
('PK', 2014, 2, 'Rajkumar Hirani', 'Alien questions Earth religions'),
('Chennai Express', 2013, 2, 'Rohit Shetty', 'Man accidentally boards wrong train'),
('Housefull', 2010, 2, 'Sajid Khan', 'Unlucky man marriage attempts'),
('Bhool Bhulaiyaa', 2007, 2, 'Priyadarshan', 'Psychiatrist investigates haunted palace'),
('Stree', 2018, 2, 'Amar Kaushik', 'Town haunted by female spirit'),

-- Thriller
('Kahaani', 2012, 7, 'Sujoy Ghosh', 'Pregnant woman searches for missing husband'),
('Andhadhun', 2018, 7, 'Sriram Raghavan', 'Blind pianist witnesses murder'),
('Drishyam', 2015, 7, 'Nishikant Kamat', 'Family covers up accidental crime'),
('Talaash', 2012, 7, 'Reema Kagti', 'Cop investigates mysterious death'),
('Special 26', 2013, 7, 'Neeraj Pandey', 'Con artists pose as CBI officers'),
('A Wednesday', 2008, 7, 'Neeraj Pandey', 'Common man holds city hostage'),
('Badla', 2019, 7, 'Sujoy Ghosh', 'Businesswoman accused of murder'),
('Ugly', 2013, 7, 'Anurag Kashyap', 'Girl goes missing adults blame each other'),
('NH10', 2015, 7, 'Navdeep Singh', 'Couple encounters violent gang'),
('Raman Raghav 2.0', 2016, 7, 'Anurag Kashyap', 'Serial killer and corrupt cop'),

-- SOUTH INDIAN CINEMA
-- Telugu (using Epic genre_id = 31)
('RRR', 2022, 31, 'S.S. Rajamouli', 'Two revolutionaries fight British rule'),
('Pushpa', 2021, 1, 'Sukumar', 'Laborer rises in red sandalwood smuggling'),
('Ala Vaikunthapurramuloo', 2020, 14, 'Trivikram Srinivas', 'Middle-class man discovers true identity'),
('Arjun Reddy', 2017, 6, 'Sandeep Reddy Vanga', 'Surgeon self-destructs after breakup'),
('Maharshi', 2019, 3, 'Vamshi Paidipally', 'CEO returns to help farmers'),
('Saaho', 2019, 1, 'Sujeeth', 'Undercover cop in crime syndicate'),
('Sye Raa Narasimha Reddy', 2019, 27, 'Surender Reddy', 'Freedom fighter battles British'),
('Rangasthalam', 2018, 27, 'Sukumar', 'Deaf man fights village president'),
('Bharat Ane Nenu', 2018, 3, 'Koratala Siva', 'Young man becomes chief minister'),
('Geetha Govindam', 2018, 28, 'Parasuram', 'Lecturer falls for independent woman'),

-- Tamil
('Vikram', 2022, 1, 'Lokesh Kanagaraj', 'Retired cop investigates serial killings'),
('Master', 2021, 1, 'Lokesh Kanagaraj', 'Professor clashes with gangster in juvenile home'),
('Kaithi', 2019, 1, 'Lokesh Kanagaraj', 'Ex-convict fights drug cartel'),
('96', 2018, 6, 'C. Prem Kumar', 'School sweethearts reunite after years'),
('Ratsasan', 2018, 7, 'Ram Kumar', 'Cop hunts psychopathic killer'),
('Asuran', 2019, 3, 'Vetrimaaran', 'Father and son flee from landlords'),
('Bigil', 2019, 20, 'Atlee', 'Gangster coaches women football team'),
('Mersal', 2017, 3, 'Atlee', 'Doctor fights medical corruption'),
('Theri', 2016, 1, 'Atlee', 'Cop protects daughter from gang'),
('Kabali', 2016, 1, 'Pa. Ranjith', 'Aged gangster seeks revenge'),

-- Malayalam
('Drishyam Malayalam', 2013, 7, 'Jeethu Joseph', 'Family covers perfect crime'),
('Premam', 2015, 6, 'Alphonse Puthren', 'Man three love stories'),
('Bangalore Days', 2014, 3, 'Anjali Menon', 'Three cousins in Bangalore'),
('Kumbalangi Nights', 2019, 3, 'Madhu C. Narayanan', 'Four brothers and their lives'),
('The Great Indian Kitchen', 2021, 3, 'Jeo Baby', 'Newly wed woman faces patriarchy'),
('Malik', 2021, 13, 'Mahesh Narayanan', 'Community leader life story'),
('Lucifer', 2019, 1, 'Prithviraj Sukumaran', 'Power struggle after leader death'),
('Charlie', 2015, 6, 'Martin Prakkat', 'Girl searches for mysterious artist'),
('Maheshinte Prathikaaram', 2016, 2, 'Dileesh Pothan', 'Photographer seeks simple revenge'),
('Thondimuthalum Driksakshiyum', 2017, 7, 'Dileesh Pothan', 'Couple chases thief on bus');

-- Sample ratings (using IGNORE to avoid duplicates)
INSERT IGNORE INTO ratings (user_id, movie_id, score, review_text) VALUES
(1, 1, 5, 'Mind-blowing action movie!'),
(1, 2, 5, 'One of the best post-apocalyptic films'),
(1, 3, 4, 'Great action sequences'),
(2, 1, 4, 'Great special effects and story'),
(2, 4, 5, 'Classic sci-fi masterpiece'),
(3, 5, 3, 'Good but a bit overrated'),
(3, 6, 5, 'Absolutely loved it!'),
(4, 10, 4, 'Classic Spielberg adventure'),
(5, 15, 5, 'One of the best sci-fi movies'),
(6, 40, 5, 'Sholay is legendary!'),
(6, 50, 4, 'DDLJ - timeless romance'),
(7, 60, 5, 'Dangal is truly inspiring'),
(8, 70, 4, 'Hera Pheri is hilarious'),
(9, 80, 5, 'Kahaani kept me on edge'),
(10, 1, 4, 'Great movie, must watch');