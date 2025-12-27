package jp.co.tisa.atg.system.database.Z102TblMente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;

import jp.co.tisa.atg.base.BCutSheetTablePageBeanBase;
import jp.co.tisa.atg.base.common.interceptor.PageBaseInterceptor;
import jp.co.tisa.atg.base.common.util.function.AtgUtils;
import jp.co.tisa.atg.base.common.util.function.CsvReader;
import jp.co.tisa.atg.base.common.util.function.LafitUtils;
import jp.co.tisa.atg.base.control.trans.TransParameter;
import jp.co.tisa.atg.base.dao.Condition;
import jp.co.tisa.atg.base.dao.Dao;
import jp.co.tisa.atg.base.dao.RecordSet;
import jp.co.tisa.atg.bl.code.SubCode;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ejb.SeamInterceptor;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * テーブルメンテナンス Actionクラス
 * <p>
 * @author 村田
 * 
 * 作成日	:	2009/11/15
 * 更新日	:	-
 * 更新履歴		更新日		担当者		内容
 * 1.0			20091115	村田		新規作成
 */
@Stateful
@Name("Z102TblMente")
@Interceptors(SeamInterceptor.class)
@PageBaseInterceptor
@TransactionManagement(TransactionManagementType.BEAN)
public class Z102TblMenteAction extends BCutSheetTablePageBeanBase implements Z102TblMente {

	//***************************
	// 業務メニュー
	//***************************
	/** 処理区分 */
    private String prsKbn;
    
    //***************************
	// ヘッダ項目の設定
	//***************************
    /** テーブルID */
    private String tblnm;
    
    /** テーブル日本語名 */
    private String tbljnm;
    
    /** 選択カラムNO */
    private String clmno;
    
    //***************************
	// 内部項目の設定
	//***************************
    /** 排他用UPDCNT */
    private Long upcnt=0L;
    
    /** テーブル日本語名(アップロード) */
    private String upLoadTbljnm="";
    
	/** 会社コード */
	private String kc = "";
	
	//***************************
	// 画面表示用List
	//***************************
	@DataModel
	private List<Z102TblMenteBean> Z102TblMenteList= new ArrayList<Z102TblMenteBean>();
	
    /**
	 * デフォルトコンストラクタ
	 * <p>
	 * 画面起動前にCALLされます<br>
	 * 初期化を行います。<br>
	 * 
	 */
	public Z102TblMenteAction() throws Exception {
		super();
		
		//ページカウントの初期化
		setPageCnt(0);
		setOrgPage(0);
		
		//ページサイズ設定（1ページ15行）
		setPageSize(12);
		
		//次ページ有り設定
		setHasMore(false);
		
		//ヘッダ部・画面タイトルを指定します
		setMainTitle("テーブルメンテナンス");
		
		//ヘッダ部・画面ＩＤを指定します
		setScreenId("Z102");
		
		//EntityManagerFactoryを使用有無設定、通常はtrueを指定してください
		setEmfOn(true);
		
		//faces-configのＩＤを指定します
		setViewId("Z102TblMente");
		
		kc = getLoginBean().getKc();
	}
	
    /**
     * 起動時・業務メニュー
     * <p>
     * 画面起動後処理を記述します。<br>
     * 画面起動時にCALLされます。<br>
     * 
     * @param tp TransParameter 画面遷移制御用パラメタクラス
     * @return 表示ページ
     * @throws Exception 例外
     */
    @Override
    public String subMenu(TransParameter tp) throws Exception{
		
		//prsKbnに取得した処理区分をセットします
		switch (tp.getKbn()){
		case '1':		//新規
		case '2':		//修正
		case '3':		//削除
		case '4':		//照会
			this.setPrsKbn(AtgUtils.convString(tp.getKbn()));
			break;
		default:		//デフォルトは照会
			this.setPrsKbn("4");
			super.setShowMenu("");
			break;
		}
		
		//画面パラメータの取得
		String p0 = getGparam(0); //０番目パラメータの取得
		if (AtgUtils.checkMust(p0)) {
			//存在しない処理区分を指定された場合、4.照会とする
			if (!p0.equals("1") && !p0.equals("2") && !p0.equals("3") && !p0.equals("4")) {
				p0 = "4";
			}
			prsKbn = p0;
		}
		String p1 = getGparam(1); //１番目パラメータの取得
		if (AtgUtils.checkMust(p1)) {
			tblnm = p1;
		}
		
		//パラメータがセットされている場合の処理
		if (AtgUtils.checkMust(p0) || AtgUtils.checkMust(p1)) {
			//パラメータがセットされている場合
			
			//表示コマンドの作成
			setCommand(prsKbn+","+tblnm);
			
			//自動押下の実行（JSPのf4ファンクションを実行）
			setShowMenu("f4()");
		}
	    
        return getViewId();
    }
    

	/**
	 * ヘッダ処理
	 * <p>
	 * ヘッダ部チェックを記述します。<br>
	 * JSPのcreateItem,editItem,selectItemが実行されると、CALLされます。<br>
	 * addError処理にてエラー設定を行うと、ボディ部へは遷移しません。<br>
	 * 
	 * @param 	tp TransParameter 画面遷移制御用パラメタクラス
	 * @throws Exception 例外
	 */
	@Override
	public void subHeadCheck(TransParameter tp) throws Exception{
		
		//データベースを使用する準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		//表示項目の初期化
		Z102TblMenteList.clear();
		
		if (!AtgUtils.checkMust(tblnm)) {
			//テーブルIDが入力されていません
			addError("Z102E01", "tblnm", "");
		} else {
			//テーブルIDが入力されている場合
			String sql = "";
			sql += " SELECT TBLNM,TBLJNM,UPCNT FROM ZM_TBL";
			sql += " WHERE KC='"+kc+"' AND TBLNM='"+tblnm+"'";
			cond.setSQL(sql);
			rs = dao.select(cond);
			boolean ret = rs.next();
			if (ret == true) {
				tbljnm = rs.getStringValue("TBLJNM");
				upcnt = Long.parseLong(rs.getStringValue("UPCNT"));
			} else {
				tbljnm = "";
				upcnt = 0L;
			}
			
			switch (tp.getKbn()){
			case '1':		//新規
				// 存在する場合エラー
				if (ret == true) {
					addError("Z102E02", "tblnm", tblnm);
				}
				if (tblnm.length() < 3) {
					addError("Z102E09", "tblnm", "");
				} else {
					//コードマスタ取得用・共通クラス
					SubCode sc = new SubCode();
					//システム名称
					sc.readCode(dao, kc, "001", tblnm.substring(0,1));
					if (sc.getCdnm()==null) {
						addError("Z102E07", "tblnm", tblnm.substring(0,1));
					}
					//種別名称
					sc.readCode(dao, kc, "002", tblnm.substring(1,2));
					if (sc.getCdnm()==null) {
						addError("Z102E08", "tblnm", tblnm.substring(1,2));
					}
				}
				break;
			case '2':		//修正
			case '3':		//削除
			case '4':		//照会
				if (ret == false) {
					// 存在しない場合、エラー
					addError("Z102E03", "tblnm", tblnm);
				}
				break;
			}
			rs.close();
			
			if (!isErrorMessages()) {
				//エラーがない場合
				
				//ページカウントの初期化
				setPageCnt(0);
				setOrgPage(0);
				
				//ワークエリアの作成
				makeTblitmWork();
				//画面項目の作成
				makeTblitmList();
			}
			
		}
		
		//クリア処理
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
		setCommand(tp.getKbn()+","+tblnm);
	}

    /**
	 * 画面項目作成
	 * <p>
	 * ZW_TBLITMを順次読込み、画面リストを作成します
	 * 
	 * @throws Exception 例外
	 */
	private void makeTblitmList() throws Exception {
		
		//データベースを使用する準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		//テーブルIDが入力されている場合
		String sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZW_TBLITM";
		sql += " WHERE KC='"+kc+"' AND CLIP='"+getLoginBean().getClip()+"' AND SY='"+getLoginBean().getSycd()+"'";
		sql += " ORDER BY TBLNM,TBLNO";
		sql += " LIMIT "+getPageCnt()*getPageSize()+","+((getPageCnt()+1)*getPageSize()+1);
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		int linecnt = 0;
		setHasMore(false);
		
		while (rs.next()) {
			if (getPageSize() > linecnt) {
				//画面明細を定義
				Z102TblMenteBean setCol = new Z102TblMenteBean();
				setCol.setTblnm(rs.getStringValue("TBLNM"));
				setCol.setTblno(rs.getStringValue("TBLNO"));
				setCol.setRnm(rs.getStringValue("RNM"));
				setCol.setBnm(rs.getStringValue("BNM"));
				setCol.setKata(rs.getStringValue("KATA"));
				setCol.setLng1(rs.getStringValue("LNG1"));
				setCol.setLng2(rs.getStringValue("LNG2"));
				setCol.setHsu(rs.getStringValue("HSU"));
				setCol.setDflt(rs.getStringValue("DFLT"));
				setCol.setTkey1(rs.getStringValue("TKEY01"));
				setCol.setTkey2(rs.getStringValue("TKEY02"));
				setCol.setTkey3(rs.getStringValue("TKEY03"));
				setCol.setTkey4(rs.getStringValue("TKEY04"));
				setCol.setTkey5(rs.getStringValue("TKEY05"));
				setCol.setTkey6(rs.getStringValue("TKEY06"));
				setCol.setTkey7(rs.getStringValue("TKEY07"));
				setCol.setTkey8(rs.getStringValue("TKEY08"));
				setCol.setTkey9(rs.getStringValue("TKEY09"));
				setCol.setTkey10(rs.getStringValue("TKEY10"));
				setCol.setTkey11(rs.getStringValue("TKEY11"));
				setCol.setTkey12(rs.getStringValue("TKEY12"));
				setCol.setTkey13(rs.getStringValue("TKEY13"));
				setCol.setTkey14(rs.getStringValue("TKEY14"));
				setCol.setTkey15(rs.getStringValue("TKEY15"));
				setCol.setTkey16(rs.getStringValue("TKEY16"));
				setCol.setTkey17(rs.getStringValue("TKEY17"));
				setCol.setTkey18(rs.getStringValue("TKEY18"));
				setCol.setTkey19(rs.getStringValue("TKEY19"));
				setCol.setTkey20(rs.getStringValue("TKEY20"));
				setCol.setBiko(rs.getStringValue("BIKO"));
				setCol.setUpcnt(Long.parseLong(rs.getStringValue("UPCNT")));
				Z102TblMenteList.add(setCol);
			} else {
				setHasMore(true);
			}
			linecnt++;
		}
//		log.info("");
	}

    /**
	 * ワークエリア作成
	 * <p>
	 * ZM_TBLITMを順次読込み、ZW_TBLITMを作成します
	 * 
	 * @throws Exception 例外
	 */
	private void makeTblitmWork() throws Exception {
		//データベースを使用する準備
		Condition cond = new Condition();
		
		//ワークエリア削除
		String sqlDel = "DELETE FROM ZW_TBLITM";
		sqlDel += " WHERE KC='"+kc+"' AND CLIP='"+getLoginBean().getClip()+"' AND SY='"+getLoginBean().getSycd()+"'";
		cond.setSQL(sqlDel);
		int rows = dao.execute(cond);
		log.info("delete rows:"+rows);

		//データINSERT
		//ZM_TBLITM作成
		String sql  = "SELECT ";
		sql += " TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+tblnm+"'";
		sql += " ORDER BY TBLNO";
		log.info(sql);
		cond.setSQL(sql);
		RecordSet rs = dao.select(cond);
		Long maxTblno=0L;
		Long lineNo=0L;
		while(rs.next()) {
			sql  = "INSERT INTO ZW_TBLITM";
			sql += "(KC,CLIP,SY,TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
			sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
			sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
			sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT)";
			sql += " VALUES (";
			sql += " '"+kc+"'";
			sql += ",'"+getLoginBean().getClip().replace("\'", "\\'")+"'";
			sql += ",'"+getLoginBean().getSycd().replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TBLNM").replace("\'", "\\'")+"'";
			sql += ","+rs.getStringValue("TBLNO").replace("\'", "\\'");
			sql += ",'"+rs.getStringValue("RNM").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("BNM").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("KATA").replace("\'", "\\'")+"'";
			sql += ","+rs.getStringValue("LNG1").replace("\'", "\\'");
			sql += ","+rs.getStringValue("LNG2").replace("\'", "\\'");
			sql += ",'"+rs.getStringValue("HSU").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("DFLT").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY01").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY02").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY03").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY04").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY05").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY06").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY07").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY08").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY09").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY10").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY11").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY12").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY13").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY14").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY15").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY16").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY17").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY18").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY19").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("TKEY20").replace("\'", "\\'")+"'";
			sql += ",'"+rs.getStringValue("BIKO").replace("\'", "\\'")+"'";
			sql += ","+rs.getStringValue("UPCNT").replace("\'", "\\'")+")";
			log.info(sql);
			cond.setSQL(sql);
			dao.execute(cond);
			maxTblno=Long.parseLong(rs.getStringValue("TBLNO"));
			lineNo++;
		}
		
		Long lastPageLine = lineNo % getPageSize();
		Long makeWorkLine = getPageSize() - lastPageLine;
		log.info(makeWorkLine.toString());
		
		//1行10明細満たない場合、残りの空ワークを作成する
		makeBlunkLine(makeWorkLine, maxTblno);
		
	}
	
    /**
     * ブランク行作成
     * <p>
     * @param Long makeWorkLine 作成する行数
     * @param Long maxTblno 開始tblno
     * @throws Exception 例外
     */
    private void makeBlunkLine(Long makeWorkLine, Long maxTblno) throws Exception{
		//データベースを使用する準備
		Condition cond = new Condition();
		
    	String sql = "";
		for (int i=0; i<makeWorkLine; i++) {
			sql  = "INSERT INTO ZW_TBLITM";
			sql += "(KC,CLIP,SY,TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
			sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
			sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
			sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT)";
			sql += " VALUES";
			sql += "('"+kc+"','"+getLoginBean().getClip()+"','"+getLoginBean().getSycd()+"'";
			sql += ",'"+tblnm+"',"+(maxTblno+i+1)+",'','','',0,0,'',''";
			sql += ",'','','','','','',''";
			sql += ",'','','','','','',''";
			sql += ",'','','','','','','',0)";
			log.info(sql);
			cond.setSQL(sql);
			dao.execute(cond);
		}
	}
	
    /**
     * 検索処理
     * <p>
     * 前頁・次頁処理を記述します。<br>
     * 
     * @param tp TransParameter 画面遷移制御用パラメタクラス
     * @return 表示ページ
     * @throws Exception 例外
     */
    @Override
    public String subFind(TransParameter tp) throws Exception{
    	//画面項目チェック
		displayCheck();
		
		//アップロードTbljnmが登録されている場合、書き換える
		if (AtgUtils.checkMust(upLoadTbljnm)) {
			tbljnm = upLoadTbljnm;
			upLoadTbljnm = "";
		}
		
		if (!isErrorMessages()) {
			//エラーがない場合
			
	    	//現在の表示状態をワークに出力
			updateTblitmWork();
			
			//表示項目の初期化
			Z102TblMenteList.clear();
			
			//画面項目の作成
	    	makeTblitmList();
		} else {
			//エラーの場合、ページを元のページに戻す
        	setPageCnt(getOrgPage());
		}
    	
        return getViewId();
	}

    /**
	 * チェック処理（ボディ部）
	 * <p>
	 * ボディ部チェックを記述します。<br>
	 * JSPのcheckが実行されると、CALLされます。<br>
	 * addError処理にてエラー設定を行うと、確定前状態へは遷移しません。<br>
	 * 
	 * @param tp TransParameter
	 * @throws Exception 例外
	 */
	@Override
	public void subBodyCheck(TransParameter tp) throws Exception {
		
		//画面項目チェック
		displayCheck();
		
    	//現在の表示状態をワークに出力
		updateTblitmWork();
		
		//ワークテーブルチェック
		workTableCheck();
		
		//エラーが無い場合
		if (!isErrorMessages()) {
			//更新します。よろしいですか？
			addError("Z999I01","","");
		}
		
		setCommand(tp.getKbn()+","+tblnm);
	}
	
    /**
	 * ワークエリア更新
	 * <p>
	 * 画面WKより、ZW_TBLITMを変更します
	 * 
	 * @throws Exception 例外
	 */
	private void updateTblitmWork() throws Exception {
		log.info("updateTblitmWork():開始");
		//データベースを使用する準備
		Condition cond = new Condition();
		
		for(Z102TblMenteBean col:Z102TblMenteList) {
			
			//プルダウン項目の取得
			//（操作不能状態のため、取得するとnullになってしまうため）
			String kata = "";
			if (LafitUtils.checkMust(col.getKata())) {
				kata = col.getKata();
			}
			String tkey01 = "";
			if (LafitUtils.checkMust(col.getTkey1())) {
				tkey01 = col.getTkey1();
			}
			String hsu = "";
			if (LafitUtils.checkMust(col.getHsu())) {
				hsu = col.getHsu();
			}
			
			String sql = "";
			sql += "UPDATE ZW_TBLITM SET";
			sql += " RNM='"+col.getRnm().replace("\'", "\\'")+"'";
			sql += ",BNM='"+col.getBnm().replace("\'", "\\'")+"'";
			sql += ",KATA='"+kata+"'";
			sql += ",LNG1='"+col.getLng1().replace("\'", "\\'")+"'";
			sql += ",LNG2='"+col.getLng2().replace("\'", "\\'")+"'";
			sql += ",HSU='"+hsu+"'";
			sql += ",DFLT='"+col.getDflt().replace("\'", "\\'")+"'";
			sql += ",BIKO='"+col.getBiko().replace("\'", "\\'")+"'";
			sql += ",TKEY01='"+tkey01+"'";
			sql += " WHERE KC='"+kc+"' AND CLIP = '"+getLoginBean().getClip()+"'";
			sql += " AND SY = '"+getLoginBean().getSycd()+"'";
			sql += " AND TBLNM = '"+col.getTblnm()+"'";
			sql += " AND TBLNO = '"+col.getTblno()+"'";
			cond.setSQL(sql);
			log.info(sql);
			dao.execute(cond);
		}
		log.info("updateTblitmWork():終了");
	}
	
    /**
	 * ワークテーブルチェック
	 * <p>
	 * ZW_TBLITMをチェックします
	 * 
	 * @throws Exception 例外
	 */
	private void workTableCheck() throws Exception {
		//データベースを使用する準備
		Condition cond = new Condition();
		RecordSet rs = null;
		
		//ワークテーブル内の項目チェック
		String sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZW_TBLITM";
		sql += " WHERE KC='"+kc+"' AND CLIP='"+getLoginBean().getClip()+"' AND SY='"+getLoginBean().getSycd()+"'";
		sql += " ORDER BY BNM,TBLNO";
		cond.setSQL(sql);
		rs = dao.select(cond);
		
		String prevBnm = "";
		String prevTblno = "";
		while (rs.next()) {
			String bnm = rs.getStringValue("BNM");
			String tblno = rs.getStringValue("TBLNO");
			String kata = rs.getStringValue("KATA");
			Long lng1 = Long.parseLong(rs.getStringValue("LNG1"));
			Long lng2 = Long.parseLong(rs.getStringValue("LNG2"));
			
			//自ワークでの重複チェック
			if (!prevBnm.equals("")) {
				if (prevBnm.equals(bnm)) {
					//項目名が重複しています
					addError("Z102E06", "", "TBLNO:"+prevTblno+","+tblno+"["+bnm+"]");
				}
			}
			
			//他テーブルへの登録済みチェック
			String ret = registCheck(dao, bnm, kata, lng1, lng2, -1L);
			if (ret != null) {
				//項目名が重複しています
				addError("Z102E04", "", "NO:"+tblno+"、項目ID:"+bnm+"「"+ret+"」");
			}
			
			prevBnm = bnm;
			prevTblno = tblno;
		}
		
		rs.close();
	}
	
    /**
	 * 画面項目チェック
	 * <p>
	 * 画面入力項目をチェックします
	 * 
	 * @throws Exception 例外
	 */
	private void displayCheck() throws Exception {
		log.info("displayCheck():開始");
		
		Long index = 0L;
		for(Z102TblMenteBean col:Z102TblMenteList) {
			
			//未入力チェック
			if (AtgUtils.checkMust(col.getBnm())) {
				//項目名
				if (!AtgUtils.checkMust(col.getRnm())) {
					//値が未入力です
					addError("Z102E05", index.toString()+"_rnm", "項目名");
				}
				//型
				if (!AtgUtils.checkMust(col.getKata())) {
					//値が未入力です
					addError("Z102E05", index.toString()+"_bnm", "型");
				}
				//長さ１
				if (!AtgUtils.checkMust(col.getLng1())) {
					//値が未入力です
					addError("Z102E05", index.toString()+"_lng1", "長さ１");
				}
				//長さ２
				if (!AtgUtils.checkMust(col.getLng2())) {
					//値が未入力です
					addError("Z102E05", index.toString()+"_lng2", "長さ２");
				}
				//長さ１・２のZEROチェック
				if (AtgUtils.checkMust(col.getKata())) {
					if (AtgUtils.checkMust(col.getLng1()) && AtgUtils.checkMust(col.getLng2())) {
						//長さ0.0はエラー
						if (col.getLng1().equals("0") && col.getLng2().equals("0")) {
							//但し、TimeStampは除外する
							if (!col.getKata().equals("T")) {
								//値が未入力です
								addError("Z102E05", index.toString()+"_lng1", "長さ");
							}
						}
					}
				}
				//長さ2チェック
				if (!col.getKata().equals("9")) {
					if (AtgUtils.parseLong(col.getLng2()) != 0) {
						//長さ2(小数点以下)は9項目のみ指定できます
						addError("Z102E10", index.toString()+"_lng2", "");
					}
				}
				//TimeStampチェック
				if (col.getKata().equals("T")) {
					if (AtgUtils.parseLong(col.getLng1()) != 0 || AtgUtils.parseLong(col.getLng2()) != 0) {
						//Timestamp型Tは、長さを指定する事ができません
						addError("Z102E11", index.toString()+"_lng1", "");
					}
				}
			}
			
			//重複チェック
			for(Z102TblMenteBean col2:Z102TblMenteList) {
				if (!col2.getTblno().equals(col.getTblno())) {
					if (col2.getBnm().equals(col.getBnm())) {
						if (!col2.getBnm().equals("")) {
							//項目IDが重複しています
							addError("Z102E06", index.toString()+"_bnm", col.getBnm());
						}
					}
				}
			}
			
			//他テーブルへの登録済みチェック
			registCheck(dao, col.getBnm(),col.getKata(),Long.parseLong(col.getLng1()),Long.parseLong(col.getLng2()),index);
			
			index++;
		}
		
		log.info("displayCheck():終了");
	}

	/**
	 * 他テーブルへの登録済みチェック
	 * <p>
	 * 入力された項目が既に他のテーブルに別の型として登録されていないかをチェックします<br>
	 * 
	 * @throws Exception 例外
	 */
	private String registCheck(Dao dao, String bnm, String kata, long lng1, long lng2, long index) throws Exception {
		//データベースを使用する準備
		Condition cond = new Condition();
		
		String ret=null;
		
		//登録済みチェック
		String sql = "";
		sql += "SELECT TBLNM,KATA,LNG1,LNG2";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM <> '"+tblnm+"'";
		sql += " AND BNM='"+bnm+"'";
		sql += " ORDER BY TBLNM";
		cond.setSQL(sql);
		
		RecordSet rs = dao.select(cond);
		if (rs.next()) {
			String rkata = rs.getStringValue("KATA");
			long rlng1 = Long.parseLong(rs.getStringValue("LNG1"));
			long rlng2 = Long.parseLong(rs.getStringValue("LNG2"));
			String rtblnm = rs.getStringValue("TBLNM");
			
			if (bnm.equals("TKYK")) {
				log.info(bnm);
				log.info("rlng1["+rlng1+"]");
				log.info("rlng2["+rlng2+"]");
				log.info("lng1["+lng1+"]");
				log.info("lng2["+lng2+"]");
			}
			
			boolean check = false;
			if (rkata.equals(kata)){
				if (rlng1 == lng1) {
					if (rlng2 == lng2) {
						check = true;
					}
				}
			}
			if (check == false) {
				//入力された項目名はすでに、別の型にて登録されています
				ret = rtblnm+":"+rkata+"("+rlng1+","+rlng2+")";
				String msg = "項目名:"+bnm+" 登録済情報「"+ret+"」";
				if (index >= 0) {
					addError("Z102E04", index+"_bnm", msg);
				}
			}
		}
		rs.close();
		cond.clear();
		return ret;
	}

	/**
	 * Command1
	 * <p>
	 * 項目間に１行データを挿入します<br>
	 * 
	 * @throws Exception 例外
	 */
	@Override
	public void subCommand1() throws Exception {
    	//現在の表示状態をワークに出力
		updateTblitmWork();
		
		//データベースを使用する準備
		Condition cond = new Condition();
		
		if (LafitUtils.checkMust(clmno)) {
			// 1行づつ順番に取り出してUPDATEを行う
			String sql = "";
			sql += " SELECT TBLNM,TBLNO FROM ZW_TBLITM ";
			sql += " WHERE KC='"+kc+"' AND CLIP = '"+getLoginBean().getClip()+"'";
			sql += " AND SY = '"+getLoginBean().getSycd()+"'";
			sql += " AND TBLNO >= '"+Z102TblMenteList.get(Integer.parseInt(clmno)).getTblno()+"'";
			sql += " ORDER BY TBLNO DESC";
			log.info(sql);
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			int lastUpdateNo = 0;
			while(rs.next()) {
				int updateNo = Integer.parseInt(rs.getStringValue("TBLNO"));
				sql  = " UPDATE ZW_TBLITM SET TBLNO='"+(updateNo+1)+"'";
				sql += " WHERE KC='"+kc+"' AND CLIP = '"+getLoginBean().getClip()+"'";
				sql += " AND SY = '"+getLoginBean().getSycd()+"'";
				sql += " AND TBLNO = '"+updateNo+"'";
				log.info(sql);
				cond.setSQL(sql);
				dao.execute(cond);
				lastUpdateNo = updateNo;
			}
			//ワークエリア作成(INSERT～SELECTの副問い合わせにて処理)
			sql  = "INSERT INTO ZW_TBLITM";
			sql += "(KC,CLIP,SY";
			sql += ",TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
			sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
			sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
			sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT)";
			sql += " VALUES ('"+kc+"','"+getLoginBean().getClip()+"','"+getLoginBean().getSycd()+"'";
			sql += ",'"+tblnm+"','"+lastUpdateNo+"','','','',0,0,'',''";
			sql += ",'','','','','','',''";
			sql += ",'','','','','','',''";
			sql += ",'','','','','','','',0)";
			log.info(sql);
			cond.setSQL(sql);
			dao.execute(cond);
		}
		
		//表示項目の初期化
		Z102TblMenteList.clear();
		
		//画面項目の作成
    	makeTblitmList();
	}

	/**
	 * Command2
	 * <p>
	 * 明細を１行削除します<br>
	 * 
	 * @throws Exception 例外
	 */
	@Override
	public void subCommand2() throws Exception {
    	//現在の表示状態をワークに出力
		updateTblitmWork();
		
		//データベースを使用する準備
		Condition cond = new Condition();
		
		//１行削除
		String sql = "";
		sql += " DELETE FROM ZW_TBLITM ";
		sql += " WHERE KC='"+kc+"' AND CLIP = '"+getLoginBean().getClip()+"'";
		sql += " AND SY = '"+getLoginBean().getSycd()+"'";
		sql += " AND TBLNO = '"+Z102TblMenteList.get(Integer.parseInt(clmno)).getTblno()+"'";
		log.info(sql);
		cond.setSQL(sql);
		dao.execute(cond);
		
		//表示項目の初期化
		Z102TblMenteList.clear();
		
		//画面項目の作成
    	makeTblitmList();
	}

	/**
	 * Command3
	 * <p>
	 * 最終ページを作成します<br>
	 * 
	 * @throws Exception 例外
	 */
	@Override
	public void subCommand3() throws Exception {

    	//画面項目チェック
		displayCheck();
		
		//アップロードTbljnmが登録されている場合、書き換える
		if (AtgUtils.checkMust(upLoadTbljnm)) {
			tbljnm = upLoadTbljnm;
			upLoadTbljnm = "";
		}
		
		if (!isErrorMessages()) {
			//エラーがない場合
			
			//現在の表示状態をワークに出力
			updateTblitmWork();
			
			//データベースを使用する準備
			Condition cond = new Condition();
			
			//TBLNOの最大値を取得
			String sql = "";
			sql += " SELECT MAX(TBLNO) AS MAXTBLNO FROM ZW_TBLITM ";
			sql += " WHERE KC='"+kc+"' AND CLIP = '"+getLoginBean().getClip()+"'";
			sql += " AND SY = '"+getLoginBean().getSycd()+"'";
			sql += " AND TBLNM = '"+tblnm+"'";
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			
			//TBLNOの最大値を取り出す
			Long maxTblno = 0L;
			if (rs.next()) {
				maxTblno = Long.parseLong(rs.getStringValue("MAXTBLNO"));
			}
			
			//最終ページを１ページ作成
			for (int i=0; i<getPageSize(); i++) {
				sql  = "INSERT INTO ZW_TBLITM";
				sql += "(KC,CLIP,SY,TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
				sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
				sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
				sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT)";
				sql += " VALUES";
				sql += "('"+kc+"','"+getLoginBean().getClip()+"','"+getLoginBean().getSycd()+"'";
				sql += ",'"+tblnm+"',"+(maxTblno+i+1)+",'','','',0,0,'',''";
				sql += ",'','','','','','',''";
				sql += ",'','','','','','',''";
				sql += ",'','','','','','','',0)";
				log.info(sql);
				cond.setSQL(sql);
				dao.execute(cond);
			}
			
			//次頁にカウントアップ
			setPageCnt(getPageCnt()+1);
			
			//表示項目の初期化
			Z102TblMenteList.clear();
			
			//画面項目の作成
	    	makeTblitmList();
	    	
		} else {
			//エラーの場合、ページを元のページに戻す
        	setPageCnt(getOrgPage());
		}
		
		
		
	}
	
	/**
	 * Command4
	 * <p>
	 * 最終ページを表示します<br>
	 * 
	 * @throws Exception 例外
	 */
	@Override
	public void subCommand4() throws Exception {

    	//画面項目チェック
		displayCheck();
		
		if (!isErrorMessages()) {
			//エラーがない場合
			
			//現在の表示状態をワークに出力
			updateTblitmWork();
			
			//データベースを使用する準備
			Condition cond = new Condition();
			
			//TBLNOの最大値を取得
			String sql = "";
			sql += " SELECT COUNT(TBLNO) AS CNTTBLNO FROM ZW_TBLITM ";
			sql += " WHERE KC='"+kc+"' AND CLIP = '"+getLoginBean().getClip()+"'";
			sql += " AND SY = '"+getLoginBean().getSycd()+"'";
			sql += " AND TBLNM = '"+tblnm+"'";
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			
			//TBLNOの最大値を取り出す
			int cntTblno = 0;
			if (rs.next()) {
				cntTblno = Integer.parseInt(rs.getStringValue("CNTTBLNO"));
			}
			
			int maxpage = cntTblno / getPageSize();
			int amari = cntTblno % getPageSize();
			
			if (amari != 0) {
				maxpage = maxpage + 1;
			}
			
			//次頁にカウントアップ
			setPageCnt(maxpage - 1);
			
			//表示項目の初期化
			Z102TblMenteList.clear();
			
			//画面項目の作成
	    	makeTblitmList();
	    	
		} else {
			//エラーの場合、ページを元のページに戻す
        	setPageCnt(getOrgPage());
		}
	}
	
	/**
	 * 更新処理
	 * <p>
	 * 更新処理を記述します。<br>
	 * JSPのupdateが実行されると、CALLされます。<br>
	 * addError処理にてエラー設定を行うと、確定処理は行われません。<br>
	 * 
	 * @param tp ステータス（遷移パターン）タイプ
	 * @return 表示ページ
	 * @throws Exception 例外
	 */
	@Override
	public void subUpdate(TransParameter tp) throws Exception {
		//データベースを使用する準備
		Condition cond = new Condition();
		
		//新規・修正のみ
		if (tp.getKbn()=='1' || tp.getKbn()=='2') {
			//既存情報削除
			String sql = "";
			sql += " DELETE FROM ZM_TBLITM ";
			sql += " WHERE KC='"+kc+"' AND TBLNM = '"+tblnm+"'";
			log.info(sql);
			cond.setSQL(sql);
			dao.execute(cond);
			
			//データINSERT
			//ZM_TBLITM作成
			sql  = "SELECT ";
			sql += " TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
			sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
			sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
			sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
			sql += " FROM ZW_TBLITM";
			sql += " WHERE KC='"+kc+"' AND TBLNM='"+tblnm+"' AND BNM<>''";
			sql += "   AND CLIP='"+getLoginBean().getClip()+"' AND SY='"+getLoginBean().getSycd()+"'";
			sql += " ORDER BY TBLNO";
			log.info(sql);
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			int tblno = 1;
			while(rs.next()) {
				sql  = "INSERT INTO ZM_TBLITM";
				sql += "(KC,TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
				sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
				sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
				sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT)";
				sql += " VALUES (";
				sql += " '"+kc+"'";
				sql += ",'"+rs.getStringValue("TBLNM").replace("\'", "\\'")+"'";
				sql += ","+tblno;
				sql += ",'"+rs.getStringValue("RNM").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("BNM").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("KATA").replace("\'", "\\'")+"'";
				sql += ","+rs.getStringValue("LNG1").replace("\'", "\\'");
				sql += ","+rs.getStringValue("LNG2").replace("\'", "\\'");
				sql += ",'"+rs.getStringValue("HSU").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("DFLT").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY01").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY02").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY03").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY04").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY05").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY06").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY07").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY08").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY09").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY10").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY11").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY12").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY13").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY14").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY15").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY16").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY17").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY18").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY19").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("TKEY20").replace("\'", "\\'")+"'";
				sql += ",'"+rs.getStringValue("BIKO").replace("\'", "\\'")+"'";
				sql += ","+rs.getStringValue("UPCNT").replace("\'", "\\'")+")";
				log.info(sql);
				cond.setSQL(sql);
				dao.execute(cond);
				tblno++;
			}
		}
		
		//更新件数
		int rows = 0;
		//SQL文
		String sql = "";
		
		switch (tp.getKbn()){
		case '1':		//新規
			sql  = "INSERT INTO ZM_TBL";
			sql += "(KC,TBLNM,TBLJNM,SYS,SHU,UPCNT,UPDTIME) VALUES (";
			sql += " '"+kc+"'";
			sql += ",'"+tblnm+"'";
			sql += ",'"+tbljnm+"'";
			sql += ",'"+tblnm.substring(0,1)+"'";
			sql += ",'"+tblnm.substring(1,2)+"'";
			sql += ",0";
			sql += ",'"+AtgUtils.getNowTime()+"')";
			log.info(sql);
			cond.setSQL(sql);
			rows = dao.execute(cond);
			if (rows == 0) {
				//他端末にて更新されました
				addError("Z999E01", "", "");
			}
			break;
			
		case '2':		//修正
			//排他用親テーブル更新
			Long upcnt2 = upcnt + 1;
			if (upcnt2 > 999) {
				upcnt2 = 0L;
			}
			sql  = " UPDATE ZM_TBL SET UPCNT="+upcnt2+",TBLJNM='"+tbljnm+"'";
			sql += " WHERE KC='"+kc+"' AND TBLNM='"+tblnm+"' AND UPCNT="+upcnt;
			log.info(sql);
			cond.setSQL(sql);
			rows = dao.execute(cond);
			if (rows == 0) {
				//他端末にて更新されました
				addError("Z999E01", "", "");
			}
			break;
			
		case '3':		//削除
			//ZM_TBLITMの削除
			sql  = " DELETE FROM ZM_TBLITM ";
			sql += " WHERE KC='"+kc+"' AND TBLNM = '"+tblnm+"'";
			log.info(sql);
			cond.setSQL(sql);
			dao.execute(cond);
			
			//ZM_TBLの削除
			sql  = " DELETE FROM ZM_TBL ";
			sql += " WHERE KC='"+kc+"' AND TBLNM = '"+tblnm+"' AND UPCNT="+upcnt;
			log.info(sql);
			cond.setSQL(sql);
			rows = dao.execute(cond);
			if (rows == 0) {
				//他端末にて更新されました
				addError("Z999E01", "", "");
			}
			break;
		}
		
		//エラーが無い場合
		if (!isErrorMessages()) {
			//更新しました
			addError("Z999I02","","");
		}
		
		setCommand(tp.getKbn()+","+tblnm);
	}
	
	/**
	 * 戻る処理
     * <p>
     * このメソッドを削除すると、確定→戻るにて、再度業務画面が開きます<br>
     * 通常は実装してください。<br>
     * 
     * @return 表示ページ
	 */
	@Override
	public String subCancel() {
		return  getCallOrigin();
	}
	
	/**
	 * アップロードリスナ
     * <p>
     * ファイルアップロードを完了すると、このメソッドが実行されます<br/>
	 */
    public void listener(UploadEvent event) throws Exception{
    	
    	//アップロードリスナは自分で作成したpublicメソッドであるため
    	//トランザクション管理を自分で開始する
    	
    	//トランザクションを行うために、daoをエンティティーマネージャーより取得する
		dao = new Dao(getEntityManager().getConnection());
		
		//データベース読み書きのための準備
		Condition cond = new Condition();
    	
        try {
        	
        	//トランザクション開始
            begin();

            Z102TblMenteList.clear();
            
    		//ワークエリア削除
    		String sqlDel = "DELETE FROM ZW_TBLITM";
    		sqlDel += " WHERE KC='"+kc+"' AND CLIP='"+getLoginBean().getClip()+"' AND SY='"+getLoginBean().getSycd()+"'";
    		cond.setSQL(sqlDel);
    		int rows = dao.execute(cond);
    		log.info("delete rows:"+rows);
        	
        	//アップロードアイテムを取得
            UploadItem item = event.getUploadItem();
            
            //アップロードアイテムをファイルとして取得
            File file = item.getFile();
            String filename = item.getFileName();
            
            //BufferdReaderとして再定義する
            FileReader fr = new FileReader(file);
            file.getPath();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()),"MS932"));
            
            //ファイル内容を1行単位で取得し業務処理を行う
            String line = null;
            Long line_cnt = 1L;
            Long cnt = 1L;
    		String name = "";
            while((line=br.readLine())!=null){
            	CsvReader cr = new CsvReader();
            	cr.csvSet(line);
            	if (line_cnt == 1) {
            		name = cr.readCsvItemStr();
            		log.info("テーブル名["+name+"]");
            	} else if (line_cnt == 2) {
            		log.info("テーブルID["+cr.readCsvItemStr()+"]");
            	} else if (line_cnt == 3) {
            		log.info("見出し");
            	} else {
            		//項目の取り込み
                	String sql = "";
        			sql  = "INSERT INTO ZW_TBLITM";
        			sql += "(KC,CLIP,SY,TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
        			sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
        			sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
        			sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT)";
        			sql += " VALUES (";
        			sql += " '"+kc+"'";
        			sql += ",'"+getLoginBean().getClip().replace("\'", "\\'")+"'";
        			sql += ",'"+getLoginBean().getSycd().replace("\'", "\\'")+"'";
        			sql += ",'"+tblnm+"'";
        			sql += ","+cnt;
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ","+cr.readCsvItemStr().replace("\'", "\\'");
        			sql += ","+cr.readCsvItemStr().replace("\'", "\\'");
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",''";
        			sql += ",'"+cr.readCsvItemStr().replace("\'", "\\'")+"'";
        			sql += ",0)";
        			log.info(sql);
        			cond.setSQL(sql);
        			dao.execute(cond);
            		cnt++;
            	}
            	
            	line_cnt++;
            }
            
            //リーダーのクローズ
            br.close();
            fr.close();
            log.info(filename);
            
            //取り込み明細に不足している空行を作成
            Long lineNo = line_cnt - 4;
    		Long lastPageLine = lineNo % getPageSize();
    		Long makeWorkLine = getPageSize() - lastPageLine;
            makeBlunkLine(makeWorkLine, cnt-1);
            
            //すべて正常に終了している場合、ページ数を0に戻す
            setPageCnt(0);
            setOrgPage(0);
            
            //アップロードテーブル名を登録
    		if (!AtgUtils.checkMust(tbljnm) && getPrsKbn().equals("1")) {
    			upLoadTbljnm = name;
    		}
    		
        } catch (Exception e) {
        	//エラーが発生した場合、ロールバックを行う
        	
        	//エラー情報の表示
            e.printStackTrace();
            
            //トランザクションロールバック
            rollback();
            
            //エラーを出力
            throw e;
            
        } finally {
        	//try～catch間の処理がすべて終了した場合に実行される
        	
        	//トランザクションコミット（エラーの場合、catchにてロールバック済み）
            commit();
            
            //daoのクローズ
    		dao.close();
        }
        
    }
    
	/******************************************************************************************
	 *  Getter/Setter設定 Limy→全フィールドのGetter/Setterメソッド作成にて作成してください   *
	 ******************************************************************************************/

	/**
	 * 処理区分を取得します。
	 * @return 処理区分
	 */
	/**
	 * 処理区分を取得します。
	 * @return 処理区分
	 */
	/**
	 * 処理区分を取得します。
	 * @return 処理区分
	 */
	public String getPrsKbn() {
	    return prsKbn;
	}

	/**
	 * 処理区分を設定します。
	 * @param prsKbn 処理区分
	 */
	/**
	 * 処理区分を設定します。
	 * @param prsKbn 処理区分
	 */
	/**
	 * 処理区分を設定します。
	 * @param prsKbn 処理区分
	 */
	public void setPrsKbn(String prsKbn) {
	    this.prsKbn = prsKbn;
	}

	/**
	 * テーブルIDを取得します。
	 * @return テーブルID
	 */
	public String getTblnm() {
	    return tblnm;
	}

	/**
	 * テーブルIDを設定します。
	 * @param tblnm テーブルID
	 */
	public void setTblnm(String tblnm) {
	    this.tblnm = tblnm;
	}

	/**
	 * テーブル日本語名を取得します。
	 * @return テーブル日本語名
	 */
	public String getTbljnm() {
	    return tbljnm;
	}

	/**
	 * テーブル日本語名を設定します。
	 * @param tbljnm テーブル日本語名
	 */
	public void setTbljnm(String tbljnm) {
	    this.tbljnm = tbljnm;
	}

	/**
	 * 選択カラムNOを取得します。
	 * @return 選択カラムNO
	 */
	public String getClmno() {
	    return clmno;
	}

	/**
	 * 選択カラムNOを設定します。
	 * @param clmno 選択カラムNO
	 */
	public void setClmno(String clmno) {
	    this.clmno = clmno;
	}

	/**
	 * 排他用UPDCNTを取得します。
	 * @return 排他用UPDCNT
	 */
	public Long getUpcnt() {
	    return upcnt;
	}

	/**
	 * 排他用UPDCNTを設定します。
	 * @param upcnt 排他用UPDCNT
	 */
	public void setUpcnt(Long upcnt) {
	    this.upcnt = upcnt;
	}

	/**
	 * Z102TblMenteListを取得します。
	 * @return Z102TblMenteList
	 */
	public List<Z102TblMenteBean> getZ102TblMenteList() {
	    return Z102TblMenteList;
	}

	/**
	 * Z102TblMenteListを設定します。
	 * @param Z102TblMenteList Z102TblMenteList
	 */
	public void setZ102TblMenteList(List<Z102TblMenteBean> Z102TblMenteList) {
	    this.Z102TblMenteList = Z102TblMenteList;
	}
	
}
