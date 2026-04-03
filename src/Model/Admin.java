package Model;
import java.sql.Timestamp;

public class Admin {
	private String maAdmin, hoTen, chuyenMon, chucVu;
    private Timestamp ngayDongBo;
    public Admin() {}
	public String getMaAdmin() {
		return maAdmin;
	}
	public void setMaAdmin(String maAdmin) {
		this.maAdmin = maAdmin;
	}
	public String getHoTen() {
		return hoTen;
	}
	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}
	public String getChuyenMon() {
		return chuyenMon;
	}
	public void setChuyenMon(String chuyenMon) {
		this.chuyenMon = chuyenMon;
	}
	public String getChucVu() {
		return chucVu;
	}
	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}
	public Timestamp getNgayDongBo() {
		return ngayDongBo;
	}
	public void setNgayDongBo(Timestamp ngayDongBo) {
		this.ngayDongBo = ngayDongBo;
	}
	@Override
	public String toString() {
		return "Admin [maAdmin=" + maAdmin + ", hoTen=" + hoTen + ", chuyenMon=" + chuyenMon + ", chucVu=" + chucVu
				+ ", ngayDongBo=" + ngayDongBo + ", getMaAdmin()=" + getMaAdmin() + ", getHoTen()=" + getHoTen()
				+ ", getChuyenMon()=" + getChuyenMon() + ", getChucVu()=" + getChucVu() + ", getNgayDongBo()="
				+ getNgayDongBo() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}
