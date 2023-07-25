/* 채팅방 */
create table chat_room
(
    `id`              bigint auto_increment primary key comment '채팅방 ID',
    `openChatRoomId`  varchar(36) not null comment '카카오 오픈채팅방 ID',
    `title`           varchar(30) not null comment '채팅방 이름',
    `coverImage`      varchar(255) comment '채팅방 커버 이미지',
    `contractAddress` varchar(50) not null comment 'NFT Contract Address',
    `status`          tinyint     not null comment '채팅방 상태 1: 활성화, 2:비활성화, 3: 삭제',
    `source`          tinyint     not null comment '채팅방 소스 0: Klaytn, 1:KlipDrops',
    `creatorId`       int         not null comment '생성자 아이디',
    `updaterId`       int comment '수정자 아이디',
    `createdAt`       datetime    not null default current_timestamp,
    `updatedAt`       datetime null,
    unique key unique_openChatRoomId (`openChatRoomId`)
);

/* 채팅방 5개 입력 */
insert into chat_room(`openChatRoomId`, `title`, `coverImage`, contractaddress, status, source, creatorid, updaterid)
values ('94ab69a6-f74a-472e-bba7-d66f89dc4875', '오페라의 유령 모여라~', '/membership/cover-2332438002.jpg', '0xa005e82487fb629923b9598fffd1c2e9499f0cab',
        1, 1, 1, null);
insert into chat_room(`openChatRoomId`, `title`, `coverImage`, contractaddress, status, source, creatorid, updaterid)
values ('d55d2cc8-0c7f-46e0-8a6e-c9e3e1cd86ac', 'Seoul See Beach', '/membership/cover-30504504040.jpg', '0xa005e82487fb629923b9598fffd1c2e9499f0cab',
        1, 0, 2, null);
insert into chat_room(`openChatRoomId`, `title`, `coverImage`, contractaddress, status, source, creatorid, updaterid)
values ('2ccfdfa9-d3a8-4a53-af45-e45a14f45b05', 'Jordan 23', '/membership/cover-4r353.jpg', '0xa005e82487fb629923b9598fffd1c2e9499f0cab',
        1, 1, 1, null);
insert into chat_room(`openChatRoomId`, `title`, `coverImage`, contractaddress, status, source, creatorid, updaterid)
values ('88e6438f-6681-4eac-b0b5-39646a0a49ab', 'Winnie the Pooh', '/membership/cover-657567576756.jpg', '0xa005e82487fb629923b9598fffd1c2e9499f0cab',
        2, 0, 3, null);
insert into chat_room(`openChatRoomId`, `title`, `coverImage`, contractaddress, status, source, creatorid, updaterid)
values ('1e52d92d-3a2e-4028-845e-75b3264cd386', 'Nyan Cat NFT', '/membership/cover-4353343311.jpg', '0xa005e82487fb629923b9598fffd1c2e9499f0cab',
        3, 1, 1, null);
