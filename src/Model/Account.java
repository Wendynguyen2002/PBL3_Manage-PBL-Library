package Model;
import java.sql.Timestamp;

public class Account {
	private String id;
    private String email;
    private String matKhauHash;
    private String soDienThoai;
    private String anhDaiDien;
    private String role; // AD, GV, SV
    private String trangThai; // HOATDONG, KHOA
    private Timestamp ngayTao;

    public Account() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMatKhauHash() {
		return matKhauHash;
	}

	public void setMatKhauHash(String matKhauHash) {
		this.matKhauHash = matKhauHash;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	public String getAnhDaiDien() {
		return anhDaiDien;
	}

	public void setAnhDaiDien(String anhDaiDien) {
		this.anhDaiDien = anhDaiDien;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public Timestamp getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(Timestamp ngayTao) {
		this.ngayTao = ngayTao;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", email=" + email + ", matKhauHash=" + matKhauHash + ", soDienThoai="
				+ soDienThoai + ", anhDaiDien=" + anhDaiDien + ", role=" + role + ", trangThai=" + trangThai
				+ ", ngayTao=" + ngayTao + ", getId()=" + getId() + ", getEmail()=" + getEmail() + ", getMatKhauHash()="
				+ getMatKhauHash() + ", getSoDienThoai()=" + getSoDienThoai() + ", getAnhDaiDien()=" + getAnhDaiDien()
				+ ", getRole()=" + getRole() + ", getTrangThai()=" + getTrangThai() + ", getNgayTao()=" + getNgayTao()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	

}
