package Ex03;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public void xuatVienVaThanhToan(int maBenhNhan, double tienVienPhi) {
        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psTruTien = null;
        PreparedStatement psUpdateGiuong = null;
        PreparedStatement psUpdateBenhNhan = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlCheck = "SELECT balance FROM Patient_Wallet WHERE patient_id = ?";
            psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setInt(1, maBenhNhan);

            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Bệnh nhân không tồn tại!");
            }

            double soDu = rs.getDouble("balance");

            if (soDu < tienVienPhi) {
                throw new SQLException("Số dư không đủ để thanh toán!");
            }

            String sql1 = "UPDATE Patient_Wallet SET balance = balance - ? WHERE patient_id = ?";
            psTruTien = conn.prepareStatement(sql1);
            psTruTien.setDouble(1, tienVienPhi);
            psTruTien.setInt(2, maBenhNhan);

            int r1 = psTruTien.executeUpdate();

            if (r1 == 0) {
                throw new SQLException("Không trừ được tiền!");
            }

            String sql2 = "UPDATE Bed SET status = 'EMPTY' WHERE patient_id = ?";
            psUpdateGiuong = conn.prepareStatement(sql2);
            psUpdateGiuong.setInt(1, maBenhNhan);

            int r2 = psUpdateGiuong.executeUpdate();

            if (r2 == 0) {
                throw new SQLException("Không cập nhật được giường!");
            }

            String sql3 = "UPDATE Patient SET status = 'DISCHARGED' WHERE patient_id = ?";
            psUpdateBenhNhan = conn.prepareStatement(sql3);
            psUpdateBenhNhan.setInt(1, maBenhNhan);

            int r3 = psUpdateBenhNhan.executeUpdate();

            if (r3 == 0) {
                throw new SQLException("Không cập nhật được trạng thái bệnh nhân!");
            }
            conn.commit();
            System.out.println("Xuất viện và thanh toán thành công!");

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
                if (psCheck != null) psCheck.close();
                if (psTruTien != null) psTruTien.close();
                if (psUpdateGiuong != null) psUpdateGiuong.close();
                if (psUpdateBenhNhan != null) psUpdateBenhNhan.close();

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
