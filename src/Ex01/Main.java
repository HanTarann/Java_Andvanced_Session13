package Ex01;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//P1: Vì autoCommit mặc đinh là true nên sau mỗi câu lệnh SQL được thực thi, nó sẽ tự động commit. Điều này có nghĩa là nếu có lỗi xảy ra sau khi một câu lệnh đã được thực thi thành công, các thay đổi trước đó sẽ không bị rollback và sẽ vẫn tồn tại trong cơ sở dữ liệu. Điều này có thể dẫn đến dữ liệu không nhất quán nếu có lỗi xảy ra trong quá trình thực thi nhiều câu lệnh liên tiếp.
public class Main {
    public void capPhatThuoc(int medicineId, int patientId) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sql1 = "UPDATE Medicine_Inventory SET quantity = quantity - 1 WHERE medicine_id = ?";
            ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, medicineId);

            int rows1 = ps1.executeUpdate();
            if (rows1 == 0) {
                throw new SQLException("Không tìm thấy thuốc để trừ!");
            }

            String sql2 = "INSERT INTO Prescription_History(patient_id, medicine_id, dispensed_date) VALUES (?, ?, NOW())";
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, patientId);
            ps2.setInt(2, medicineId);

            int rows2 = ps2.executeUpdate();
            if (rows2 == 0) {
                throw new SQLException("Không lưu được lịch sử!");
            }
            conn.commit();
            System.out.println("Cấp phát thuốc thành công!");

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Đã rollback do lỗi!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Lỗi: " + e.getMessage());
        } finally {
            try {
                if (ps1 != null)
                    ps1.close();
                if (ps2 != null)
                    ps2.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
