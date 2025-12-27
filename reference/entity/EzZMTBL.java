package jp.co.tisa.atg.entity;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import jp.co.tisa.atg.base.EntityBeanBase;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * テーブルマスタ・エンティティＢｅａｎ
 * <p>
 *
 * @author 村田
 */
@Entity
@Name("ezzmtbl")
@Scope(SESSION)
@Table(name="ZM_TBL")
@SuppressWarnings("serial")
public class EzZMTBL extends EntityBeanBase implements Serializable
{
	/**
	 * テーブル名
	 */
	@Id
	private String tblnm;

	/**
	 * テーブル名（日本語）
	 */
	private String tbljnm;

	/**
	 * システム区分
	 */
	private String sys;

	/**
	 * テーブル種別
	 */
	private String shu;

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
	public EzZMTBL() {}

	/**
	 * テーブル名を取得します。
	 * @return テーブル名
	 */
	public String getTblnm() {
	    return tblnm;
	}

	/**
	 * テーブル名を設定します。
	 * @param tblnm テーブル名
	 */
	public void setTblnm(String tblnm) {
	    this.tblnm = tblnm;
	}

	/**
	 * テーブル名（日本語）を取得します。
	 * @return テーブル名（日本語）
	 */
	public String getTbljnm() {
	    return tbljnm;
	}

	/**
	 * テーブル名（日本語）を設定します。
	 * @param tbljnm テーブル名（日本語）
	 */
	public void setTbljnm(String tbljnm) {
	    this.tbljnm = tbljnm;
	}

	/**
	 * システム区分を取得します。
	 * @return システム区分
	 */
	public String getSys() {
	    return sys;
	}

	/**
	 * システム区分を設定します。
	 * @param sys システム区分
	 */
	public void setSys(String sys) {
	    this.sys = sys;
	}

	/**
	 * テーブル種別を取得します。
	 * @return テーブル種別
	 */
	public String getShu() {
	    return shu;
	}

	/**
	 * テーブル種別を設定します。
	 * @param shu テーブル種別
	 */
	public void setShu(String shu) {
	    this.shu = shu;
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
