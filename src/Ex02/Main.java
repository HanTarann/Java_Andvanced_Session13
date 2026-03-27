package Ex02;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//P1: Chỉ log lỗi bằng System.out.println()
//Không gọi rollback()
public class Main {
    public void thanhToanVienPhi(int patientId, int invoiceId, double amount) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            String sql1 = "UPDATE Patient_Wallet SET balance = balance - ? WHERE patient_id = ?";
            ps1 = conn.prepareStatement(sql1);
            ps1.setDouble(1, amount);
            ps1.setInt(2, patientId);

            int rows1 = ps1.executeUpdate();
            if (rows1 == 0) {
                throw new SQLException("Không trừ được tiền!");
            }

            String sql2 = "UPDATE Invoices SET status = 'PAID' WHERE invoice_id = ?";
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, invoiceId);

            int rows2 = ps2.executeUpdate();
            if (rows2 == 0) {
                throw new SQLException("Không cập nhật được hóa đơn!");
            }

            conn.commit();
            System.out.println("Thanh toán thành công!");

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
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();

                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
