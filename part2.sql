create database Family_Db
go
use Family_Db
go

create table Person_Details
(
Person_Id varchar(10) primary key,
Personal_Name varchar(15),
Family_Name varchar(15),
Gender bit,
Father_Id varchar(10), 
Mother_Id varchar(10), 
Spouse_Id varchar(10)
)
go


create table Family_Tree
(
Person_Id varchar(10) foreign key references Person_Details(Person_Id),
Relative_Id varchar(10), 
Connection_Type varchar(10),
constraint pk_family_tree primary key (Person_Id,Relative_Id)
)
go

create or alter procedure AddPersonUpdateTree
(@P_Id varchar(10),@P_Name varchar(15),@Family_Name varchar(15),@Gender bit,@F_Id varchar(10),@M_Id varchar(10),@S_Id varchar(10))
as
begin tran
	begin try
		insert into Person_Details values(@P_Id,@P_Name,@Family_Name,@Gender,@F_Id,@M_Id,@S_Id)
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Person_Id,Father_Id,'Father' from Person_Details where Person_Id=@P_Id
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Person_Id,Mother_Id,'Mother' from Person_Details where Person_Id=@P_Id
		
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Daughter' from Person_Details p1 join Person_Details p2 on
		(p1.Person_Id=p2.Father_Id or p1.Person_Id=p2.Mother_Id) and p2.Gender=1 where p1.Person_Id=@P_Id
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Son' from Person_Details p1 join Person_Details p2 on
		(p1.Person_Id=p2.Father_Id or p1.Person_Id=p2.Mother_Id )and p2.Gender=0 where p1.Person_Id=@P_Id
		
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Daughter' from Person_Details p1 join Person_Details p2 on
		(p1.Person_Id=p2.Father_Id or p1.Person_Id=p2.Mother_Id) and p2.Gender=1 where p2.Person_Id=@P_Id
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Son' from Person_Details p1 join Person_Details p2 on
		(p1.Person_Id=p2.Father_Id or p1.Person_Id=p2.Mother_Id) and p2.Gender=0 where  p1.Person_Id=@P_Id
		
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Person_Id,Spouse_Id,'Hasband' from Person_Details where Gender=1 and Spouse_Id is not null and Person_Id=@P_Id
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Person_Id,Spouse_Id,'Wife' from Person_Details where Gender=0 and Spouse_Id is not null and Person_Id=@P_Id
		
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Sister' from Person_Details p1 join Person_Details p2 on
		(p1.Father_Id=p2.Father_Id or p1.Mother_Id=p2.Mother_Id)and p1.Person_Id<>p2.Person_Id and p2.Gender=1 where p1.Person_Id=@P_Id
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Brother' from Person_Details p1 join Person_Details p2 on
		(p1.Father_Id=p2.Father_Id or p1.Mother_Id=p2.Mother_Id)and p1.Person_Id<>p2.Person_Id and p2.Gender=0 where p1.Person_Id=@P_Id	
	
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Sister' from Person_Details p1 join Person_Details p2 on
		(p1.Father_Id=p2.Father_Id or p1.Mother_Id=p2.Mother_Id)and p1.Person_Id<>p2.Person_Id and p2.Gender=1 where p2.Person_Id=@P_Id
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select p1.Person_Id,p2.Person_Id,'Brother' from Person_Details p1 join Person_Details p2 on
		(p1.Father_Id=p2.Father_Id or p1.Mother_Id=p2.Mother_Id)and p1.Person_Id<>p2.Person_Id and p2.Gender=0 where p2.Person_Id=@P_Id	

		exec UpdateSpouse @P_Id, @S_Id
	end try

	begin catch
		rollback
		declare @ErrorMessage nvarchar(4000), @ErrorSeverity int, @ErrorState int
		select @ErrorMessage = ERROR_MESSAGE(), @ErrorSeverity = ERROR_SEVERITY(), @ErrorState = ERROR_STATE()
		RAISERROR (@ErrorMessage, @ErrorSeverity, @ErrorState)
	end catch
commit
 


 go
 create or alter procedure UpdateSpouse(@p_Id varchar(10),@S_Id varchar(10))
 as
 begin tran
	begin try
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Spouse_Id,Person_Id,'Hasband' from Person_Details where @P_ID=Spouse_Id and Gender=0 and @S_Id is null
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Spouse_Id,Person_Id,'Wife' from Person_Details where @P_ID=Spouse_Id and Gender=1 and @S_Id is null

		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Person_Id,@S_Id,'Wife' from Person_Details where @S_ID=Person_Id and Gender=0 and Spouse_Id is null
		insert into Family_Tree(Person_Id, Relative_Id, Connection_Type)
		select Person_Id,@S_Id,'Hasband' from Person_Details where @S_ID=Person_Id and Gender=1 and Spouse_Id is null
	end try

	begin catch
		rollback
		declare @ErrorMessage nvarchar(4000), @ErrorSeverity int, @ErrorState int
		select @ErrorMessage = ERROR_MESSAGE(), @ErrorSeverity = ERROR_SEVERITY(), @ErrorState = ERROR_STATE()
		RAISERROR (@ErrorMessage, @ErrorSeverity, @ErrorState)
	end catch
commit

exec  AddPersonUpdateTree '037619434','Malky','Davidovitz',1,'987654321','123456789','027290659'
exec  AddPersonUpdateTree '215129990','Miri','Farkash',1,'027290659','037619434',null
exec  AddPersonUpdateTree '027290659','Eliezer','Davidovitz',0,'123654789','987456321','037619434'
exec  AddPersonUpdateTree '327533642','Yosi','Farkash',0,'321654987','789456123','215129990'
exec  AddPersonUpdateTree '215006725','Rachel','Davidovitz',1,'027290659','037619434',null



select * from Family_Tree
select * from Person_Details
delete from Family_Tree
delete from Person_Details