package Ex05;
import util.DBConnection;

import java.sql.*;

public class Main {
    public class BenhNhanController {
        public boolean tiepNhan(String ten, int tuoi, int maGiuong, double tien) {
            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);

                String checkBed = "SELECT status FROM Giuong WHERE id = ?";
                PreparedStatement psCheck = conn.prepareStatement(checkBed);
                psCheck.setInt(1, maGiuong);
                ResultSet rs = psCheck.executeQuery();

                if (!rs.next() || !"EMPTY".equals(rs.getString("status"))) {
                    throw new Exception("Giường không hợp lệ");
                }
                String sql1 = "INSERT INTO BenhNhan(ten, tuoi) VALUES (?, ?)";
                PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, ten);
                ps1.setInt(2, tuoi);
                ps1.executeUpdate();

                ResultSet key = ps1.getGeneratedKeys();
                key.next();
                int benhNhanId = key.getInt(1);

                String sql2 = "UPDATE Giuong SET status = 'OCCUPIED' WHERE id = ?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, maGiuong);
                if (ps2.executeUpdate() == 0) throw new Exception();

                String sql3 = "INSERT INTO TaiChinh(benhNhanId, soTien) VALUES (?, ?)";
                PreparedStatement ps3 = conn.prepareStatement(sql3);
                ps3.setInt(1, benhNhanId);
                ps3.setDouble(2, tien);
                if (ps3.executeUpdate() == 0) throw new Exception();

                conn.commit();
                return true;

            } catch (Exception e) {
                try {
                    if (conn != null) conn.rollback();
                } catch (Exception ex) {}
                return false;
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (Exception e) {}
            }
        }
    }
}
