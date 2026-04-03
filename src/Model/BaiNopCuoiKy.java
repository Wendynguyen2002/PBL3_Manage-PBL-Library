package Model;
import java.sql.Timestamp;

public class BaiNopCuoiKy {
    private String maBaiNop, maNhom, maMilestone, maNguoiNop, tieuDe, tomTat, trangThai, duongDanFile, tenFileHeThong;
    private int soLanCheck;
    private double tyLeTuongDong;
    private boolean deXuatThuVien;
    private Timestamp ngayDang;
    public BaiNopCuoiKy() {}
	public String getMaBaiNop() {
		return maBaiNop;
	}
	public void setMaBaiNop(String maBaiNop) {
		this.maBaiNop = maBaiNop;
	}
	public String getMaNhom() {
		return maNhom;
	}
	public void setMaNhom(String maNhom) {
		this.maNhom = maNhom;
	}
	public String getMaMilestone() {
		return maMilestone;
	}
	public void setMaMilestone(String maMilestone) {
		this.maMilestone = maMilestone;
	}
	public String getMaNguoiNop() {
		return maNguoiNop;
	}
	public void setMaNguoiNop(String maNguoiNop) {
		this.maNguoiNop = maNguoiNop;
	}
	public String getTieuDe() {
		return tieuDe;
	}
	public void setTieuDe(String tieuDe) {
		this.tieuDe = tieuDe;
	}
	public String getTomTat() {
		return tomTat;
	}
	public void setTomTat(String tomTat) {
		this.tomTat = tomTat;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public String getDuongDanFile() {
		return duongDanFile;
	}
	public void setDuongDanFile(String duongDanFile) {
		this.duongDanFile = duongDanFile;
	}
	public String getTenFileHeThong() {
		return tenFileHeThong;
	}
	public void setTenFileHeThong(String tenFileHeThong) {
		this.tenFileHeThong = tenFileHeThong;
	}
	public int getSoLanCheck() {
		return soLanCheck;
	}
	public void setSoLanCheck(int soLanCheck) {
		this.soLanCheck = soLanCheck;
	}
	public double getTyLeTuongDong() {
		return tyLeTuongDong;
	}
	public void setTyLeTuongDong(double tyLeTuongDong) {
		this.tyLeTuongDong = tyLeTuongDong;
	}
	public boolean isDeXuatThuVien() {
		return deXuatThuVien;
	}
	public void setDeXuatThuVien(boolean deXuatThuVien) {
		this.deXuatThuVien = deXuatThuVien;
	}
	public Timestamp getNgayDang() {
		return ngayDang;
	}
	public void setNgayDang(Timestamp ngayDang) {
		this.ngayDang = ngayDang;
	}
	@Override
	public String toString() {
		return "BaiNopCuoiKy [maBaiNop=" + maBaiNop + ", maNhom=" + maNhom + ", maMilestone=" + maMilestone
				+ ", maNguoiNop=" + maNguoiNop + ", tieuDe=" + tieuDe + ", tomTat=" + tomTat + ", trangThai="
				+ trangThai + ", duongDanFile=" + duongDanFile + ", tenFileHeThong=" + tenFileHeThong + ", soLanCheck="
				+ soLanCheck + ", tyLeTuongDong=" + tyLeTuongDong + ", deXuatThuVien=" + deXuatThuVien + ", ngayDang="
				+ ngayDang + ", getMaBaiNop()=" + getMaBaiNop() + ", getMaNhom()=" + getMaNhom() + ", getMaMilestone()="
				+ getMaMilestone() + ", getMaNguoiNop()=" + getMaNguoiNop() + ", getTieuDe()=" + getTieuDe()
				+ ", getTomTat()=" + getTomTat() + ", getTrangThai()=" + getTrangThai() + ", getDuongDanFile()="
				+ getDuongDanFile() + ", getTenFileHeThong()=" + getTenFileHeThong() + ", getSoLanCheck()="
				+ getSoLanCheck() + ", getTyLeTuongDong()=" + getTyLeTuongDong() + ", isDeXuatThuVien()="
				+ isDeXuatThuVien() + ", getNgayDang()=" + getNgayDang() + "]";
	}
	
    
}