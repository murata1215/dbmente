package jp.co.tisa.atg.entity;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

import jp.co.tisa.atg.base.EntityBeanBase;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * テーブル項目マスタ・エンティティＢｅａｎ
 * <p>
 *
 * @author 村田
 */
@Entity
@Name("ezzmtblitm")
@Scope(SESSION)
@Table(name="ZM_TBLITM")
@IdClass(jp.co.tisa.atg.entity.EzZMTBLITMPK.class)
@SuppressWarnings("serial")
public class EzZMTBLITM extends EntityBeanBase implements Serializable
{
	/**
	 * テーブル名
	 */
	@Id
	private String tblnm;

	/**
	 * 項目ＮＯ
	 */
	@Id
	private long tblno;

	/**
	 * 論理名
	 */
	private String rnm;

	/**
	 * 物理名
	 */
	private String bnm;

	/**
	 * 型
	 */
	private String kata;

	/**
	 * 長さ１
	 */
	private long lng1;

	/**
	 * 長さ２
	 */
	private long lng2;

	/**
	 * 必須
	 */
	private String hsu;

	/**
	 * デフォルト値
	 */
	private String dflt;

	/**
	 * キー１
	 */
	private String tkey01;

	/**
	 * キー２
	 */
	private String tkey02;

	/**
	 * キー３
	 */
	private String tkey03;

	/**
	 * キー４
	 */
	private String tkey04;

	/**
	 * キー５
	 */
	private String tkey05;

	/**
	 * キー６
	 */
	private String tkey06;

	/**
	 * キー７
	 */
	private String tkey07;

	/**
	 * キー８
	 */
	private String tkey08;

	/**
	 * キー９
	 */
	private String tkey09;

	/**
	 * キー１０
	 */
	private String tkey10;

	/**
	 * キー１１
	 */
	private String tkey11;

	/**
	 * キー１２
	 */
	private String tkey12;

	/**
	 * キー１３
	 */
	private String tkey13;

	/**
	 * キー１４
	 */
	private String tkey14;

	/**
	 * キー１５
	 */
	private String tkey15;

	/**
	 * キー１６
	 */
	private String tkey16;

	/**
	 * キー１７
	 */
	private String tkey17;

	/**
	 * キー１８
	 */
	private String tkey18;

	/**
	 * キー１９
	 */
	private String tkey19;

	/**
	 * キー２０
	 */
	private String tkey20;

	/**
	 * 備考
	 */
	private String biko;

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
	public EzZMTBLITM() {}

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
	 * 項目ＮＯを取得します。
	 * @return 項目ＮＯ
	 */
	public long getTblno() {
	    return tblno;
	}

	/**
	 * 項目ＮＯを設定します。
	 * @param tblno 項目ＮＯ
	 */
	public void setTblno(long tblno) {
	    this.tblno = tblno;
	}

	/**
	 * 論理名を取得します。
	 * @return 論理名
	 */
	public String getRnm() {
	    return rnm;
	}

	/**
	 * 論理名を設定します。
	 * @param rnm 論理名
	 */
	public void setRnm(String rnm) {
	    this.rnm = rnm;
	}

	/**
	 * 物理名を取得します。
	 * @return 物理名
	 */
	public String getBnm() {
	    return bnm;
	}

	/**
	 * 物理名を設定します。
	 * @param bnm 物理名
	 */
	public void setBnm(String bnm) {
	    this.bnm = bnm;
	}

	/**
	 * 型を取得します。
	 * @return 型
	 */
	public String getKata() {
	    return kata;
	}

	/**
	 * 型を設定します。
	 * @param kata 型
	 */
	public void setKata(String kata) {
	    this.kata = kata;
	}

	/**
	 * 長さ１を取得します。
	 * @return 長さ１
	 */
	public long getLng1() {
	    return lng1;
	}

	/**
	 * 長さ１を設定します。
	 * @param lng1 長さ１
	 */
	public void setLng1(long lng1) {
	    this.lng1 = lng1;
	}

	/**
	 * 長さ２を取得します。
	 * @return 長さ２
	 */
	public long getLng2() {
	    return lng2;
	}

	/**
	 * 長さ２を設定します。
	 * @param lng2 長さ２
	 */
	public void setLng2(long lng2) {
	    this.lng2 = lng2;
	}

	/**
	 * 必須を取得します。
	 * @return 必須
	 */
	public String getHsu() {
	    return hsu;
	}

	/**
	 * 必須を設定します。
	 * @param hsu 必須
	 */
	public void setHsu(String hsu) {
	    this.hsu = hsu;
	}

	/**
	 * デフォルト値を取得します。
	 * @return デフォルト値
	 */
	public String getDflt() {
	    return dflt;
	}

	/**
	 * デフォルト値を設定します。
	 * @param dflt デフォルト値
	 */
	public void setDflt(String dflt) {
	    this.dflt = dflt;
	}

	/**
	 * キー１を取得します。
	 * @return キー１
	 */
	public String getTkey01() {
	    return tkey01;
	}

	/**
	 * キー１を設定します。
	 * @param tkey01 キー１
	 */
	public void setTkey01(String tkey01) {
	    this.tkey01 = tkey01;
	}

	/**
	 * キー２を取得します。
	 * @return キー２
	 */
	public String getTkey02() {
	    return tkey02;
	}

	/**
	 * キー２を設定します。
	 * @param tkey02 キー２
	 */
	public void setTkey02(String tkey02) {
	    this.tkey02 = tkey02;
	}

	/**
	 * キー３を取得します。
	 * @return キー３
	 */
	public String getTkey03() {
	    return tkey03;
	}

	/**
	 * キー３を設定します。
	 * @param tkey03 キー３
	 */
	public void setTkey03(String tkey03) {
	    this.tkey03 = tkey03;
	}

	/**
	 * キー４を取得します。
	 * @return キー４
	 */
	public String getTkey04() {
	    return tkey04;
	}

	/**
	 * キー４を設定します。
	 * @param tkey04 キー４
	 */
	public void setTkey04(String tkey04) {
	    this.tkey04 = tkey04;
	}

	/**
	 * キー５を取得します。
	 * @return キー５
	 */
	public String getTkey05() {
	    return tkey05;
	}

	/**
	 * キー５を設定します。
	 * @param tkey05 キー５
	 */
	public void setTkey05(String tkey05) {
	    this.tkey05 = tkey05;
	}

	/**
	 * キー６を取得します。
	 * @return キー６
	 */
	public String getTkey06() {
	    return tkey06;
	}

	/**
	 * キー６を設定します。
	 * @param tkey06 キー６
	 */
	public void setTkey06(String tkey06) {
	    this.tkey06 = tkey06;
	}

	/**
	 * キー７を取得します。
	 * @return キー７
	 */
	public String getTkey07() {
	    return tkey07;
	}

	/**
	 * キー７を設定します。
	 * @param tkey07 キー７
	 */
	public void setTkey07(String tkey07) {
	    this.tkey07 = tkey07;
	}

	/**
	 * キー８を取得します。
	 * @return キー８
	 */
	public String getTkey08() {
	    return tkey08;
	}

	/**
	 * キー８を設定します。
	 * @param tkey08 キー８
	 */
	public void setTkey08(String tkey08) {
	    this.tkey08 = tkey08;
	}

	/**
	 * キー９を取得します。
	 * @return キー９
	 */
	public String getTkey09() {
	    return tkey09;
	}

	/**
	 * キー９を設定します。
	 * @param tkey09 キー９
	 */
	public void setTkey09(String tkey09) {
	    this.tkey09 = tkey09;
	}

	/**
	 * キー１０を取得します。
	 * @return キー１０
	 */
	public String getTkey10() {
	    return tkey10;
	}

	/**
	 * キー１０を設定します。
	 * @param tkey10 キー１０
	 */
	public void setTkey10(String tkey10) {
	    this.tkey10 = tkey10;
	}

	/**
	 * キー１１を取得します。
	 * @return キー１１
	 */
	public String getTkey11() {
	    return tkey11;
	}

	/**
	 * キー１１を設定します。
	 * @param tkey11 キー１１
	 */
	public void setTkey11(String tkey11) {
	    this.tkey11 = tkey11;
	}

	/**
	 * キー１２を取得します。
	 * @return キー１２
	 */
	public String getTkey12() {
	    return tkey12;
	}

	/**
	 * キー１２を設定します。
	 * @param tkey12 キー１２
	 */
	public void setTkey12(String tkey12) {
	    this.tkey12 = tkey12;
	}

	/**
	 * キー１３を取得します。
	 * @return キー１３
	 */
	public String getTkey13() {
	    return tkey13;
	}

	/**
	 * キー１３を設定します。
	 * @param tkey13 キー１３
	 */
	public void setTkey13(String tkey13) {
	    this.tkey13 = tkey13;
	}

	/**
	 * キー１４を取得します。
	 * @return キー１４
	 */
	public String getTkey14() {
	    return tkey14;
	}

	/**
	 * キー１４を設定します。
	 * @param tkey14 キー１４
	 */
	public void setTkey14(String tkey14) {
	    this.tkey14 = tkey14;
	}

	/**
	 * キー１５を取得します。
	 * @return キー１５
	 */
	public String getTkey15() {
	    return tkey15;
	}

	/**
	 * キー１５を設定します。
	 * @param tkey15 キー１５
	 */
	public void setTkey15(String tkey15) {
	    this.tkey15 = tkey15;
	}

	/**
	 * キー１６を取得します。
	 * @return キー１６
	 */
	public String getTkey16() {
	    return tkey16;
	}

	/**
	 * キー１６を設定します。
	 * @param tkey16 キー１６
	 */
	public void setTkey16(String tkey16) {
	    this.tkey16 = tkey16;
	}

	/**
	 * キー１７を取得します。
	 * @return キー１７
	 */
	public String getTkey17() {
	    return tkey17;
	}

	/**
	 * キー１７を設定します。
	 * @param tkey17 キー１７
	 */
	public void setTkey17(String tkey17) {
	    this.tkey17 = tkey17;
	}

	/**
	 * キー１８を取得します。
	 * @return キー１８
	 */
	public String getTkey18() {
	    return tkey18;
	}

	/**
	 * キー１８を設定します。
	 * @param tkey18 キー１８
	 */
	public void setTkey18(String tkey18) {
	    this.tkey18 = tkey18;
	}

	/**
	 * キー１９を取得します。
	 * @return キー１９
	 */
	public String getTkey19() {
	    return tkey19;
	}

	/**
	 * キー１９を設定します。
	 * @param tkey19 キー１９
	 */
	public void setTkey19(String tkey19) {
	    this.tkey19 = tkey19;
	}

	/**
	 * キー２０を取得します。
	 * @return キー２０
	 */
	public String getTkey20() {
	    return tkey20;
	}

	/**
	 * キー２０を設定します。
	 * @param tkey20 キー２０
	 */
	public void setTkey20(String tkey20) {
	    this.tkey20 = tkey20;
	}

	/**
	 * 備考を取得します。
	 * @return 備考
	 */
	public String getBiko() {
	    return biko;
	}

	/**
	 * 備考を設定します。
	 * @param biko 備考
	 */
	public void setBiko(String biko) {
	    this.biko = biko;
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
