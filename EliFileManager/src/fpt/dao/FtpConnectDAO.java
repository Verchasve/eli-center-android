package fpt.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import fpt.util.MySqlDataAccessHelper;
import ftp.pojo.FtpConnect;

public class FtpConnectDAO {
	MySqlDataAccessHelper mysql = new MySqlDataAccessHelper();
	
	public FtpConnect getFTP(){
		FtpConnect kq=new FtpConnect();
		
		try{
			/*mysql.open();
			String sql="select * from ftp_connect where ActiveSt='yes'";
			ResultSet rs= mysql.executeQuery(sql);
			while(rs.next()){
				kq.setHostname(rs.getString("Hostname"));
				kq.setPort(rs.getInt("Port"));
				kq.setUsername(rs.getString("Username"));
				kq.setPassword(rs.getString("Password"));
			}
			rs.close();*/
			
		//	kq=ReadXMLFile.ReadXML();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mysql.close();
		}
		return kq;
	}
	
	public List<FtpConnect> getAllConnectFtpHost(){
		List<FtpConnect> lstConnect = new ArrayList<FtpConnect>();
		try{
//			mysql.open();
//			String sql ="select * from ftp_connect where ActiveSt=1";
//			ResultSet rs = mysql.executeQuery(sql);
//			while(rs.next()){
//				FtpConnect ftpConnect = new FtpConnect();
//				ftpConnect.setIdftpconnect(rs.getInt("IDFTPConnect"));
//				ftpConnect.setHostname(rs.getString("Hostname"));
//				ftpConnect.setPort(rs.getString("Port"));
//				ftpConnect.setUsername(rs.getString("Username"));
//				ftpConnect.setPassword(rs.getString("Password"));
//				ftpConnect.setLocaldir(rs.getString("Localdir"));
//				ftpConnect.setRemotedir(rs.getString("Remotedir"));
//				ftpConnect.setActivest(rs.getInt("ActiveSt"));
//				
//				lstConnect.add(ftpConnect);
//			}
//			rs.close();
			lstConnect=ReadXMLFile.ReadXML();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			//mysql.close();
		}
		return lstConnect;
	}
	
	// insert ftp connect
	public int insertFtpConnect(FtpConnect ftpConnect){
		int kq = 1;
		try{
			/*mysql.open();
			String sql = "insert into ftp_connect(IDFTPConnect,Hostname,Port,Username,Password," +
					"Localdir,Remotedir,ActiveSt) values(null,'"+ftpConnect.getHostname()+"'," +
				"'"+ftpConnect.getPort()+"','"+ftpConnect.getUsername()+"','"+ftpConnect.getPassword()+"'," +
						"'"+ftpConnect.getLocaldir()+"','"+ftpConnect.getRemotedir()+"','"+ftpConnect.getActivest()+"')";
			////System.out.prln(" sql ===" + sql);
			kq = mysql.executeUpdate(sql);*/
			ModifyXMLFile.ModifyXMLFile(ftpConnect);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			//mysql.close();
		}
		return kq;
	}
	
	// update ftp connect
	public int updateFtpConnect(FtpConnect ftpConnect){
		int kq = -1;
		try{
			mysql.open();
			String sql = "update ftp_connect set Hostname='"+ftpConnect.getHostname()+"',Port='"+ftpConnect.getPort()+"'," +
					"Username='"+ftpConnect.getUsername()+"',Password='"+ftpConnect.getPassword()+"'," +
					"Localdir='"+ftpConnect.getLocaldir()+"',Remotedir='"+ftpConnect.getRemotedir()+"'," +
					"ActiveSt='"+ftpConnect.getActivest()+"' where IDFTPConnect='"+ftpConnect.getIdftpconnect()+"'";
			////System.out.prln("sql === " + sql);
			kq = mysql.executeUpdate(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			mysql.close();
		}
		return kq;
	}
	
	// delete ftp connect
	public int deleteFtpConnect(FtpConnect connect){
		int kq = -1;
		try{
			mysql.open();
			//String sql = "delete from ftp_connect where IDFTPConnect='"+connect.getIdftpconnect()+"'";
			
			////System.out.prln("sql delete : " + sql);
			//kq = mysql.executeUpdate(sql);
				DeteleXMLFile.DeleteXMLFile(connect);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			mysql.close();
		}
		return kq;
	}
}
