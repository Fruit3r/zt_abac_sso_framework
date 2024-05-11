INSERT INTO Users (username, password, email, blocked, attributes)
VALUES ('Admin', '$2a$10$CCsxWxtdSEV/oeXJRrH5RujibFZp.SMrp3AtTT2kpyoIdExIw3n6W', 'admin@email.com', FALSE, '{"role":"root"}'),
       ('User1', '$2a$10$S8zTLfNGnzHp66QMyZbpHejYcGQOi9BPZ8pxxsQmE1LXtFxxcUmDC', 'user1@email.com', FALSE, '{}'),
       ('User2', '$2a$10$BsFErF7PLwJ2YEu2XwWNl.cFu9gWRWFWhvxdFaJxrSd4eszAo/CvO', 'user2@email.com', FALSE, '{}'),
       ('User3', '$2a$10$.T7AGBaps/w.VOz08ZZNReSsJ5uTvvyN0nbRfAUm3H4SXjyh4jPYq', 'user3@email.com', FALSE, '{}'),
       ('User4', '$2a$10$hu5SWghkj8UYq.rBoerYSea/J/IKygN20OSfjlNEc2p7fnHQh3Rke', 'user4@email.com', FALSE, '{}'),
       ('User5', '$2a$10$q7WYrmDSv3wRBTm0jNQKRuwGj19.Y6dF6PA5ZN9kevlVS4.PXwl/q', 'user5@email.com', FALSE, '{}'),
       ('User6', '$2a$10$1dpvQvsCdA82Od9yxGZGVOZ7TQCKcUnYZMmii6NJz.qWJDBYffX72', 'user6@email.com', FALSE, '{}'),
       ('User7', '$2a$10$ZlfafGNc94hhasw5GwyIie8oZyWCmQfrYdjLLNPK3Q0rg2M23ZfsC', 'user7@email.com', FALSE, '{}'),
       ('User8', '$2a$10$afJMJBHxvlMndqcnXrYm7ee7ebFEhLj7ly6b3y4FFgWq59hBRJrtW', 'user8@email.com', FALSE, '{}'),
       ('User9', '$2a$10$68A.9nwVjTd4f1nbgN00iOroUpbpMWDb.PO7/Zf6qnQgvKahy1XRS', 'user9@email.com', TRUE, '{}');

INSERT INTO Policies (name, rules)
VALUES ('root_policy', '#subject_role=="root"');