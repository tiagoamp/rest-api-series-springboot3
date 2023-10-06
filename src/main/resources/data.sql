INSERT INTO BOOKS (TITLE, LANGUAGE, YEAR_OF_PUBLICATION, AUTHORS) VALUES ('Frankenstein', 'English', 1818, 'Mary Shelley');
INSERT INTO BOOKS (TITLE, LANGUAGE, YEAR_OF_PUBLICATION, AUTHORS) VALUES ('Philosophiae Naturalis Principia Mathematica', 'Latim', 1687, 'Isaac Newton');
INSERT INTO BOOKS (TITLE, LANGUAGE, YEAR_OF_PUBLICATION, AUTHORS) VALUES ('Dom Casmurro', 'Portuguese', 1889, 'Machado de Assis');

INSERT INTO REVIEWS (BOOK_ID, TEXT) VALUES ( (SELECT ID FROM BOOKS WHERE TITLE = 'Frankenstein'), 'Test review of the book Frankstein');
INSERT INTO REVIEWS (BOOK_ID, TEXT) VALUES ( (SELECT ID FROM BOOKS WHERE TITLE = 'Frankenstein'), 'Another test review of the book Frankstein');
INSERT INTO REVIEWS (BOOK_ID, TEXT) VALUES ( (SELECT ID FROM BOOKS WHERE TITLE = 'Philosophiae Naturalis Principia Mathematica'), 'Test review of the book of Sir Isaac Newton');
INSERT INTO REVIEWS (BOOK_ID, TEXT) VALUES ( (SELECT ID FROM BOOKS WHERE TITLE = 'Philosophiae Naturalis Principia Mathematica'), 'Another test example of review');
INSERT INTO REVIEWS (BOOK_ID, TEXT) VALUES ( (SELECT ID FROM BOOKS WHERE TITLE = 'Dom Casmurro'), 'Test review of the book');
