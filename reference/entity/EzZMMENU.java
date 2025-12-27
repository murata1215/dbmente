package jp.co.tisa.atg.entity;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * メニューマスタエンティティＢｅａｎ
 * <p>
 *
 * @author 村田
 */
@Entity
@Name("ezzmmenu")
@Scope(SESSION)
@Table(name="ZM_MENU")
@IdClass(jp.co.tisa.atg.entity.EzZMMENUPK.class)
@SuppressWarnings("serial")
public class EzZMMENU implements Serializable {

	/**
	 * 会社コード
	 */
	@Id
	private String kc;

	/**
	 * メニューID
	 */
	@Id
	private String mid;

	/**
	 * 位置NO
	 */
	@Id
	private long pno;

	/**
	 * 上位メニューID
	 */
	private String upid;

	/**
	 * 表示タイトル
	 */
	private String mtitle;

	/**
	 * リンク先ID
	 */
	private String lid;

	/**
	 * リンク先区分
	 */
	private String lkb;

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
	public EzZMMENU() {
		
	}

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
	 * メニューIDを取得します。
	 * @return メニューID
	 */
	public String getMid() {
	    return mid;
	}

	/**
	 * メニューIDを設定します。
	 * @param mid メニューID
	 */
	public void setMid(String mid) {
	    this.mid = mid;
	}


	/**
	 * 位置NOを取得します。
	 * @return 位置NO
	 */
	public long getPno() {
	    return pno;
	}

	/**
	 * 位置NOを設定します。
	 * @param pno 位置NO
	 */
	public void setPno(long pno) {
	    this.pno = pno;
	}

	/**
	 * 上位メニューIDを取得します。
	 * @return 上位メニューID
	 */
	public String getUpid() {
	    return upid;
	}

	/**
	 * 上位メニューIDを設定します。
	 * @param upid 上位メニューID
	 */
	public void setUpid(String upid) {
	    this.upid = upid;
	}

	/**
	 * 表示タイトルを取得します。
	 * @return 表示タイトル
	 */
	public String getMtitle() {
	    return mtitle;
	}

	/**
	 * 表示タイトルを設定します。
	 * @param mtitle 表示タイトル
	 */
	public void setMtitle(String mtitle) {
	    this.mtitle = mtitle;
	}

	/**
	 * リンク先IDを取得します。
	 * @return リンク先ID
	 */
	public String getLid() {
	    return lid;
	}

	/**
	 * リンク先IDを設定します。
	 * @param lid リンク先ID
	 */
	public void setLid(String lid) {
	    this.lid = lid;
	}

	/**
	 * リンク先区分を取得します。
	 * @return リンク先区分
	 */
	public String getLkb() {
	    return lkb;
	}

	/**
	 * リンク先区分を設定します。
	 * @param lkb リンク先区分
	 */
	public void setLkb(String lkb) {
	    this.lkb = lkb;
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
