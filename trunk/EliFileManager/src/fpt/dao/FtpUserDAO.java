package fpt.dao;

import java.sql.ResultSet;



import fpt.util.MD5;
import fpt.util.MySqlDataAccessHelper;
import ftp.pojo.FtpUser;

public class FtpUserDAO {

	public FtpUser checkLogin(String name, String pass){
		FtpUser kq=new FtpUser();
		MySqlDataAccessHelper mysql = new MySqlDataAccessHelper();
		try{
			mysql.open();
			String sql="select * " +
					"from ftp_user " +
					"where ftp_user.UserName='"+name+"' " +
							"and ftp_user.Password='"+MD5.encrypt(pass)+"' " +
									"and ftp_user.ActiveSt=1";
			ResultSet rs= mysql.executeQuery(sql);
			while(rs.next()){
				kq.setIduser(rs.getInt("IDUser"));
				kq.setUsername(rs.getString("UserName"));
				kq.setIdrole(rs.getInt("IDRole"));
				kq.setActivest(rs.getInt("ActiveSt"));
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mysql.close();
		}
		return kq;
	}
}
