create table Customers(
    Card_id number primary key,
    Password varchar2(50),
    CName varchar2(50),
    Ph_num number,
    Balance number(10,2),
    current_status varchar2(10)
);
--current_status : new_user, active, inactive

create table Products(
    Product_id number primary key,
    PName varchar2(50),
    Price number(10,2)
);

create table Outlets(
    Outlet_id number Primary key,
    Password varchar2(50),
    location varchar2(50)
);

create table Outlet_Menus(
    Outlet_id number references Outlet_id(Outlet_id),
    Product_id number references Products(Product_id)
);

create table Bills(
    Bill_id number Primary key,
    Card_id number references Customers(Card_id),
    Outlet_ID number references Outlets(Outlet_id),
    Amount number(10,2),
    Bill_Date date
)

create table Purchases(
    Bill_id number references Bills(Bill_Date),
    Product_id number references Products(Product_id),
    Quantity number
);

create table Fin_transaction(
    Trans_id number primary key,
    Card_id number references Customers(Card_id),
    Type varchar2(10),
    Amount number,
    Trans_date date
);

create sequence c_id_seq start with 1 increment by 1 minvalue 0;

create sequence o_id_seq start with 1 increment by 1 minvalue 0;

create sequence b_id_seq start with 1 increment by 1 minvalue 0;

create sequence t_id_seq start with 1 increment by 1 minvalue 0;

create sequence p_id_seq start with 1 increment by 1 minvalue 0;

--Outlet creates customer
create or replace procedure add_customer (CName varchar2(50), Phone_num in number, status out number)
is
declare
    num_unique number;
begin
    status := 0;
    if(Phone_num > 9999999999 OR Phone_num < 1000000000) then
        status := 2;
        return;
    end if;

    select count(*) into num_unique from Customers where Ph_num = Phone_num;
    if(num_unique > 0) then
        status := 3;
        return;
    end if;

    insert into Customers values(c_id_seq.nextval, 'unset', CName, Phone_num, 0, 'new_user');
    status := 1;
    return;
end;
/

-- Update password by user or user creates password for the first time
create or replace procedure update_password (C_id in number, new_pwd in varchar2(50), status out number)
is
begin
    status := 0;
    update customers set Password = new_pwd where Card_id = C_id;
    status :=1;
    return;
end;
/

create or replace procedure match_username (Phone_num in number, pwd out varchar2(50), status out number)
is
begin
    status := 1;
    if (Phone_num > 9999999999 OR Phone_num < 1000000000) then
        status := 2;
        return;
    end if;
    select count(*) into status from Customers where Ph_num = Phone_num;
    if (status = 1) then
        select password into pwd where Card_id = C_id;
    end if; 
    return;
end;
/

create or replace procedure login_user (Phone_num in number, status out number)
is
begin
    status := 0;
    update Customers set login_status = 'active' where Ph_num = Phone_num;
    status := 1;
    return;
end;
/

create or replace procedure logout_user (Phone_num in number, status out number)
is
begin
    status := 0;
    update Customers set login_status = 'inactive' where Ph_num = Phone_num;
    status := 1;
    return;
end;
/

create or replace procedure add_product (PName in varchar2(50), Price in number(10,2), status out number)
is
begin
    status := 0;
    insert into Products values(p_id_seq.nextval, PName, Price);
    status := 1;
    return;
end;
/

create or replace procedure update_price (P_id in number, new_price in number(10,2), status out number)
is
begin
    status := 0;
    update Products set price = new_price where Product_id = P_id;
    status := 1;
    return;
end;
/

create or replace procedure create_outlet (location in varchar2(50), pwd in varchar2(50), Outlet_id out number, status out number)
is
begin
    status := 0;
    Outlet_id := o_id_seq.nextval;
    insert into Outlets values(Outlet_id, pwd, location);
    status := 1;
    return;
end;
/

create or replace procedure delete_outlet (O_id in number, status out number)
is
begin
    status := 0;
    delete Outlets where Outlet_id=O_id;
    status := 1;
    return;
end;
/

create or replace procedure add_to_menu (Outlet_id in number, Product_id in number, status out number)
is
begin
    status := 0;
    insert into Outlet_Menus values(Outlet_id, Product_id);
    status := 1;
end;
/

create or replace procedure remove_from_menu (O_id in number, P_id in number, status out number)
is
begin
    status := 0;
    delete Outlet_Menus where Outlet_id = O_id AND Product_id = P_id;
    status := 1;
end;
/

CREATE OR REPLACE TYPE MyType AS VARRAY(200) OF VARCHAR2(50);
/

create or replace type number_array as varray(100) of number;
/

create or replace procedure gen_bill (outlet_id in number, c_id in number, prods in number_array, qty in number_array, bill_id out number, amount out number, bill_date out varchar2(50),status out number)
is
declare
    amt number(10,2);
    bal number(10,2);
begin
    status := 0;
    for i in 1..products.count loop
        select Price into amt from Products where Product_id = prods(i);
        amount:= amount + qty(i)*amt;
    end loop;

    select Balance into bal from Customers where Card_id = c_id;
    if (amount < bal) then
        status := 2;
        return;
    end if;

    bill_id := b_id_seq.nextval;
    for i in 1..products.count loop
        insert into Purchases values(bill_id, prods(i), qty(i));
    end loop;

    insert into Bills values(bill_id, c_id, outlet_id, amount, sysdate);
    update Customers set Balance := Balance - amount where Card_id = c_id;
    status := 1;    
end;
/

create or replace trigger reward
before insert on Bills
for each row
declare
    bill_cnt number;
begin
    select  count(*) into bill_cnt from Bills where Card_id = :new.Card_id;
    if (bill_cnt%2 = 0) then
        insert into Fin_transaction values(t_id_seq.nextval, :new.Card_id, 'r', min(100, 0.02*:new.amount), sysdate);
    end if;
end;
/

create or replace trigger sync_bal
before insert on Fin_transaction
for each row
declare
    avail_bal number;
begin
    select Balance into avail_bal from Customers where Card_id = :new.Card_id;
    if (:new.type = 'w') then
        if(avail_bal < :new.amount) then
            raise_application_error(-20000, 'Insufficient Balance.');
        else
            update Customers set Balance = Balance - :new.amount where Card_id = :new.Card_id;
        end if;
    else
        update Customers set Balance = Balance + :new.amount where Card_id = :new.Card_id;
    end if;
end;
/