package jp.co.tisa.atg.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * メニュー項目マスタエンティティＢｅａｎ
 * <p>
 *
 * @author 村田
 */
@Entity
@Name("ezzmmenuitm")
@Scope(ScopeType.SESSION)
@Table(name="ZM_MENUITM")
@IdClass(jp.co.tisa.atg.entity.EzZMMENUITMPK.class)
@SuppressWarnings("serial")
public class EzZMMENUITM implements Serializable {

	/**
	 * 会社コード
	 */
	@Id
	private String kc;

	/**
	 * 業務画面ID
	 */
	@Id
	private String gid;

	/**
	 * 業務画面タイトル
	 */
	private String gtitle;

	/**
	 * プロジェクト
	 */
	private String project;

	/**
	 * プロジェクトサーバーIP
	 */
	private String prjsvip;

	/**
	 * 業務制限
	 */
	private String stlimit;

	/**
	 * 起動メソッド
	 */
	private String staddress;

	/**
	 * 起動パラメータ
	 */
	private String stparam;

	/**
	 * 画面説明
	 */
	private String alt;

	/**
	 * 起動制限（端末）
	 */
	private String actm;

	/**
	 * 起動制限（ユーザー）
	 */
	private String acusr;

	/**
	 * 起動制限（部門）
	 */
	private String acbu;

	/**
	 * 棚卸時業務制限
	 */
	private String tanlim;

	/**
	 * 更新回数
	 */
	@Version
	private long upcnt;

	/**
	 * 更新日時
	 */
	private Date updtime;
	
	/**
	 * デフォルトコンストラクタ
	 * <p>
	 *
	 */
	public EzZMMENUITM() {}

	/**
	 * 会社コードを取得します。
	 * @return 会社コード
	 */
	public String getKc() {
	    return kc;
	}

	/**
	 * 会社コードを設定します。
	 * @param kc 会社コード
	 */
	public void setKc(String kc) {
	    this.kc = kc;
	}

	/**
	 * 業務画面IDを取得します。
	 * @return 業務画面ID
	 */
	public String getGid() {
	    return gid;
	}

	/**
	 * 業務画面IDを設定します。
	 * @param gid 業務画面ID
	 */
	public void setGid(String gid) {
	    this.gid = gid;
	}

	/**
	 * 業務画面タイトルを取得します。
	 * @return 業務画面タイトル
	 */
	public String getGtitle() {
	    return gtitle;
	}

	/**
	 * 業務画面タイトルを設定します。
	 * @param gtitle 業務画面タイトル
	 */
	public void setGtitle(String gtitle) {
	    this.gtitle = gtitle;
	}

	/**
	 * プロジェクトを取得します。
	 * @return プロジェクト
	 */
	public String getProject() {
	    return project;
	}

	/**
	 * プロジェクトを設定します。
	 * @param project プロジェクト
	 */
	public void setProject(String project) {
	    this.project = project;
	}

	/**
	 * プロジェクトサーバーIPを取得します。
	 * @return プロジェクトサーバーIP
	 */
	public String getPrjsvip() {
	    return prjsvip;
	}

	/**
	 * プロジェクトサーバーIPを設定します。
	 * @param prjsvip プロジェクトサーバーIP
	 */
	public void setPrjsvip(String prjsvip) {
	    this.prjsvip = prjsvip;
	}

	/**
	 * 業務制限を取得します。
	 * @return 業務制限
	 */
	public String getStlimit() {
	    return stlimit;
	}

	/**
	 * 業務制限を設定します。
	 * @param stlimit 業務制限
	 */
	public void setStlimit(String stlimit) {
	    this.stlimit = stlimit;
	}

	/**
	 * 起動メソッドを取得します。
	 * @return 起動メソッド
	 */
	public String getStaddress() {
	    return staddress;
	}

	/**
	 * 起動メソッドを設定します。
	 * @param staddress 起動メソッド
	 */
	public void setStaddress(String staddress) {
	    this.staddress = staddress;
	}

	/**
	 * 起動パラメータを取得します。
	 * @return 起動パラメータ
	 */
	public String getStparam() {
	    return stparam;
	}

	/**
	 * 起動パラメータを設定します。
	 * @param stparam 起動パラメータ
	 */
	public void setStparam(String stparam) {
	    this.stparam = stparam;
	}

	/**
	 * 画面説明を取得します。
	 * @return 画面説明
	 */
	public String getAlt() {
	    return alt;
	}

	/**
	 * 画面説明を設定します。
	 * @param alt 画面説明
	 */
	public void setAlt(String alt) {
	    this.alt = alt;
	}

	/**
	 * 起動制限（端末）を取得します。
	 * @return 起動制限（端末）
	 */
	public String getActm() {
	    return actm;
	}

	/**
	 * 起動制限（端末）を設定します。
	 * @param actm 起動制限（端末）
	 */
	public void setActm(String actm) {
	    this.actm = actm;
	}

	/**
	 * 起動制限（ユーザー）を取得します。
	 * @return 起動制限（ユーザー）
	 */
	public String getAcusr() {
	    return acusr;
	}

	/**
	 * 起動制限（ユーザー）を設定します。
	 * @param acusr 起動制限（ユーザー）
	 */
	public void setAcusr(String acusr) {
	    this.acusr = acusr;
	}

	/**
	 * 起動制限（部門）を取得します。
	 * @return 起動制限（部門）
	 */
	public String getAcbu() {
	    return acbu;
	}

	/**
	 * 起動制限（部門）を設定します。
	 * @param acbu 起動制限（部門）
	 */
	public void setAcbu(String acbu) {
	    this.acbu = acbu;
	}

	/**
	 * 棚卸時業務制限を取得します。
	 * @return 棚卸時業務制限
	 */
	public String getTanlim() {
	    return tanlim;
	}

	/**
	 * 棚卸時業務制限を設定します。
	 * @param tanlim 棚卸時業務制限
	 */
	public void setTanlim(String tanlim) {
	    this.tanlim = tanlim;
	}

	/**
	 * 更新回数を取得します。
	 * @return 更新回数
	 */
	public long getUpcnt() {
	    return upcnt;
	}

	/**
	 * 更新回数を設定します。
	 * @param upcnt 更新回数
	 */
	public void setUpcnt(long upcnt) {
	    this.upcnt = upcnt;
	}

	/**
	 * 更新日時を取得します。
	 * @return 更新日時
	 */
	public Date getUpdtime() {
	    return updtime;
	}

	/**
	 * 更新日時を設定します。
	 * @param updtime 更新日時
	 */
	public void setUpdtime(Date updtime) {
	    this.updtime = updtime;
	}


}
