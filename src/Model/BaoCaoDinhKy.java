package Model;
import java.sql.Timestamp;


public class BaoCaoDinhKy {
	private String maBaoCao, maNhom, maMilestone, maNguoiNop, duongDanFile, loaiFile, trangThai;
    private int phienBan;
    private long dungLuongFile;
    private double diemBaoCao;
    private Timestamp thoiGianNop;
    private boolean isNopTre;
    public BaoCaoDinhKy() {}
	public String getMaBaoCao() {
		return maBaoCao;
	}
	public void setMaBaoCao(String maBaoCao) {
		this.maBaoCao = maBaoCao;
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
	public String getDuongDanFile() {
		return duongDanFile;
	}
	public void setDuongDanFile(String duongDanFile) {
		this.duongDanFile = duongDanFile;
	}
	public String getLoaiFile() {
		return loaiFile;
	}
	public void setLoaiFile(String loaiFile) {
		this.loaiFile = loaiFile;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	public int getPhienBan() {
		return phienBan;
	}
	public void setPhienBan(int phienBan) {
		this.phienBan = phienBan;
	}
	public long getDungLuongFile() {
		return dungLuongFile;
	}
	public void setDungLuongFile(long dungLuongFile) {
		this.dungLuongFile = dungLuongFile;
	}
	public double getDiemBaoCao() {
		return diemBaoCao;
	}
	public void setDiemBaoCao(double diemBaoCao) {
		this.diemBaoCao = diemBaoCao;
	}
	public Timestamp getThoiGianNop() {
		return thoiGianNop;
	}
	public void setThoiGianNop(Timestamp thoiGianNop) {
		this.thoiGianNop = thoiGianNop;
	}
	public boolean isNopTre() {
		return isNopTre;
	}
	public void setNopTre(boolean isNopTre) {
		this.isNopTre = isNopTre;
	}
    
}
