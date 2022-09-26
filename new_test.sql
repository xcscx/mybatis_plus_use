create table student(
	id bigint(20) not null PRIMARY KEY auto_increment comment 'id',
	studentno varchar(12) comment'学生学号',
	sname varchar(8) comment'学生姓名',
	sex varchar(2) comment'性别',
	birthdate date	comment'出生年月',
	entrance int comment'入学成绩',
	phone varchar(11) comment'电话号码',
	Email varchar(20) comment'电子邮件',
	version int comment '版本号',
	deleted boolean comment '禁用'
);


insert into student(id,studentNo,sname,sex,birthdate,entrance,phone,email,deleted)
value(1,'B20180304101','许东山','男','1999-11-05',877,13786112345,'zhang@yeha.net',false),
		 (2,'B20180304102','张明','男','1998-08-23',789,13786112347,'3245888@QQ.com',false),
		 (3,'B20180304103','王伟','男','1999-09-12',790,13786488347,'3267888@QQ.com',false),
		 (4,'B20180304104','吴英','女','2000-10-19',888,13055568618,'17865@QQ.com',false);
		 
 