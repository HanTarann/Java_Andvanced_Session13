package Ex04;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public List<BenhNhanDTO> getDanhSachBenhNhan() {
        List<BenhNhanDTO> result = new ArrayList<>();
        Map<Integer, BenhNhanDTO> map = new HashMap<>();

        String sql = "SELECT b.maBenhNhan, b.ten, d.maDichVu, d.tenDichVu " +
                "FROM BenhNhan b " +
                "LEFT JOIN DichVuSuDung d ON b.maBenhNhan = d.maBenhNhan";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int maBN = rs.getInt("maBenhNhan");

                BenhNhanDTO bn = map.get(maBN);
                if (bn == null) {
                    bn = new BenhNhanDTO();
                    bn.setMaBenhNhan(maBN);
                    bn.setTen(rs.getString("ten"));
                    bn.setDsDichVu(new ArrayList<>());
                    map.put(maBN, bn);
                }

                int maDV = rs.getInt("maDichVu");
                if (!rs.wasNull()) {
                    DichVu dv = new DichVu();
                    dv.setMaDichVu(maDV);
                    dv.setTenDichVu(rs.getString("tenDichVu"));
                    bn.getDsDichVu().add(dv);
                }
            }

            result.addAll(map.values());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
