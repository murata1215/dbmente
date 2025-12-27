package jp.co.tisa.atg.system.database.Z101TblSyokai;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.interceptor.Interceptors;

import jp.co.tisa.atg.base.BTablePageBeanBase;
import jp.co.tisa.atg.base.common.interceptor.PageBaseInterceptor;
import jp.co.tisa.atg.base.common.util.function.AtgUtils;
import jp.co.tisa.atg.base.common.util.function.LafitUtils;
import jp.co.tisa.atg.base.dao.Condition;
import jp.co.tisa.atg.base.dao.RecordSet;
import jp.co.tisa.atg.bl.code.SubCode;
import jp.co.tisa.atg.common.printer.CsvPrinter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * テーブル一覧 Actionクラス
 * <p>
 * @author 村田
 * 
 * 作成日	:	2009/11/13
 * 更新日	:	-
 * 更新履歴		更新日		担当者		内容
 * 1.0			20091113	村田		新規作成
 */
@Stateful
@Name("Z101TblSyokai")
@Interceptors(SeamInterceptor.class)
@PageBaseInterceptor
public class Z101TblSyokaiAction extends BTablePageBeanBase implements Z101TblSyokai {

	/**
	 * POSTパラメータ（DLFIND_PATTERN）格納変数
	 */
	@SuppressWarnings("unused")
	@RequestParameter("DLFIND_PATTERN")
	private String DLFIND_PATTERN;
	
	//***************************
	// 画面表示用List
	//***************************
	@DataModel
	private List<Z101TblSyokaiBean> Z101TblSyokaiList= new ArrayList<Z101TblSyokaiBean>();
	
    //***************************
	// 検索条件
	//***************************
    /** JSPへの引渡し情報　リスト表示区分 */
	private String toggle;
    /** JSPへの引渡し情報　リスト状況区分 */
	private String jokyoKbn;
    /** csv検索条件 */
	private String csvKbn;
	/** 選択カラムNo */
	private String columNo = "";
	
    //***************************
	// 検索条件
	//***************************
	/** システム区分 */
	private String sys;
	/** 種別 */
	private String shu;
	/** テーブル名 */
	private String tblnm;
	/** テーブル日本語名 */
	private String tbljnm;
	/** カラム名 */
	private String clmnm;
	/** カラム日本語名 */
	private String clmjnm;
	/** カラムリスト */
	private boolean clmList = false;
	
    //***************************
	// 内部変数
	//***************************
	/** 会社コード */
	private String kc = "";
	
	private String uniqueKey = "";
	
    /**
	 * デフォルトコンストラクタ
	 * <p>
	 * 画面起動前にCALLされます<br>
	 * 初期化を行います。<br>
	 * 
	 */
	public Z101TblSyokaiAction() throws Exception {
		super();
		
		//ページカウントの初期化
		setPageCnt(0);
		setOrgPage(0);
		
		//ページサイズ設定（1ページ10行）
		setPageSize(13);
		
		//次ページ有り設定
		setHasMore(false);
		
		//ヘッダ部・画面タイトルを指定します
		setMainTitle("テーブル一覧");

		//ヘッダ部・画面ＩＤを指定します
		setScreenId("Z101");
		
		//faces-configのＩＤを指定します
		setViewId("Z101TblSyokai");
		
		kc = getLoginBean().getKc();
	}

    /**
     * 初期処理
     * <p>
     * 画面起動後処理を記述します。<br>
     * 画面起動時にCALLされます。<br>
     * 
     * @return 表示ページ
     * @throws Exception 例外
     */
	@Override
	public String subInit() throws Exception {
		
		//項目の初期化
		sys = "";
		shu = "";
		tblnm = "";
		tbljnm = "";
		
		//画面パラメータの取得
		String p0 = getGparam(0);//０番目パラメータの取得
		if (AtgUtils.checkMust(p0)) {
			tblnm = p0;
		}
		
		//パラメータがセットされている場合の処理
		if (AtgUtils.checkMust(p0)) {
			//パラメータがセットされている場合
			
			//表示コマンドの作成
			setCommand(tblnm);
			
			//自動押下の実行（JSPのf4ファンクションを実行）
			setShowMenu("f4()");
		}
		
		return getViewId();
	}
	
	/**
	 * 検索前チェック処理
	 * <p>
	 * ヘッダ部チェックを記述します。<br>
	 * 
	 * @throws Exception 例外
	 */
	@Override
	public void subFindCheck() throws Exception {
		
		Condition cond = new Condition();
		
		if (AtgUtils.checkMust(tblnm)) {
			//テーブル名が入力されている場合
			String sql = "";
			sql += " SELECT TBLNM FROM ZM_TBL";
			sql += " WHERE  KC='"+kc+"' AND TBLNM LIKE '%"+tblnm+"%'";
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			
			// 存在しない場合、エラー
			if (!rs.next()) {
				//入力されたテーブルIDは存在しません
				addError("Z101E01", "tblnm", tblnm);
			}
			rs.close();
		}
    	
		if (AtgUtils.checkMust(tbljnm)) {
			//テーブル日本語名が入力されている場合
			String sql = "";
			sql += " SELECT TBLNM FROM ZM_TBL";
			sql += " WHERE KC='"+kc+"' AND TBLJNM LIKE '%"+tbljnm+"%'";
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			
			// 存在しない場合、エラー
			if (!rs.next()) {
				//入力されたテーブル名は存在しません
				addError("Z101E02", "tbljnm", tbljnm);
			}
			rs.close();
		}
    	
		if (AtgUtils.checkMust(clmnm)) {
			//カラム名が入力されている場合
			String sql = "";
			sql += " SELECT BNM FROM ZM_TBLITM";
			sql += " WHERE KC='"+kc+"' AND BNM LIKE '%"+clmnm+"%'";
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			
			// 存在しない場合、エラー
			if (!rs.next()) {
				//入力されたカラムIDは存在しません
				addError("Z101E03", "clmnm", tblnm);
			}
			rs.close();
		}
    	
		if (AtgUtils.checkMust(clmjnm)) {
			//カラム日本語名が入力されている場合
			String sql = "";
			sql += " SELECT RNM FROM ZM_TBLITM";
			sql += " WHERE KC='"+kc+"' AND RNM LIKE '%"+clmjnm+"%'";
			cond.setSQL(sql);
			RecordSet rs = dao.select(cond);
			
			// 存在しない場合、エラー
			if (!rs.next()) {
				//入力されたカラム名は存在しません
				addError("Z101E04", "clmjnm", clmjnm);
			}
			rs.close();
		}
    	
		if (isErrorMessages()) {
			//エラーの場合、画面リストクリア
			Z101TblSyokaiList.clear();
		}
		
		if (AtgUtils.checkMust(tblnm)) {
			//表示コマンドの作成
			setCommand(tblnm);
		}
		
		cond.clear();
		
//		AI_TESTTBL_tool att = new AI_TESTTBL_tool();
//		att.init();
//		att.setFrno("2");
//		att.setMkcode("01");
//		att.setSu(50L);
//		att.setUpcnt(0L);
//		att.setUpdtime("20091217120000");
//		try {
//			att.insert_AI_TESTTBL(dao);
//		} catch(Exception e) {
//			;
//		}
		
		
//		//ツールの定義
//		AI_GSI_tool agtSel = new AI_GSI_tool();
//		
//		//read_テーブル名
//		//プライマリキーにてレコードを読みます
//		//存在する場合true、存在しない場合falseを返します
//		if (agtSel.read_AI_GSI(dao, "6FD30X-32609", 1L, "200915", "2", "")) {
//			//存在する場合
//			
//			//delete_テーブル名
//			//プライマリキーにてレコードを削除します
//			//削除できた件数を返します
//			AI_GSI_tool agtDel = new AI_GSI_tool();
//			int cnt = agtDel.delete_AI_GSI(dao, agtSel.getFrno(), agtSel.getEb(), agtSel.getSkdate(), agtSel.getTp());
//			log.info("削除件数"+cnt);
//			
//		}
//		
//		//insert_テーブル名
//		//データを1件創生します。
//		AI_GSI_tool agtIns = new AI_GSI_tool();
//		agtIns.setAi_gsiid(100L);
//		agtIns.setFrno("6FD30X-32609");
//		agtIns.setEb(1L);
//		agtIns.setSkdate("200915");
//		agtIns.setTp("2");
//		agtIns.setKtki(90000L);
//		agtIns.setSkbu("030");
//		agtIns.setSkdt("20090101");
//		agtIns.setUpcnt(0L);
//		agtIns.setUpdtime(AtgUtils.getNowTime());
//		agtIns.insert_AI_GSI(dao);
//		
//		//list_テーブル名
//		//WHERE句を指定し、レコードセット形式でテーブルを読みます
//		AI_GSI_tool agtList = new AI_GSI_tool();
//		agtList.list_AI_GSI(dao, "SKDATE > '200912'", "SKDATE DESC", "0,20");
//		while (agtList.next()) {
//			log.info(agtList.getFrno());
//			
//			//update_テーブル名
//			//プライマリキーにてレコードを更新します
//			AI_GSI_tool agtUpd = new AI_GSI_tool();
//			agtUpd.setTp("5");
//			agtUpd.setUpcnt(agtList.getUpcnt()+1);
//			agtUpd.setUpdtime(AtgUtils.getNowTime());
//			int cntUpd = agtUpd.update_AI_GSI(dao,
//					agtList.getFrno(),
//					agtList.getEb(),
//					agtList.getSkdate(),
//					agtList.getTp(), "AND UPCNT = "+agtList.getUpcnt());
//			if (cntUpd == 0) {
//				throw new RuntimeException("他端末にて更新されました");
//			}
//		}
//		agtList.close();
		
	}
	
	/**
	 * 検索処理
	 * <p>
	 * リスト作成処理を記述します。<br>
	 * 
	 * @throws Exception 例外
	 */
	@Override
	public String subFind() throws Exception {
		
		//画面データ作成
		Z101TblSyokaiList = readData("", null);
		
		if (Z101TblSyokaiList.size() == 0) {
			//検索結果が存在しない場合、ワーニング
			String[] item = {"tblnm"};
			addError("Z101W03",item,"");
		}
		
		return getViewId();
	}
	
	/**
	 * データ検索用 内部メソッド
	 * @param mode "csv"を指定することで、csv用リストを返す(条件にマッチする、すべてのデータ)
	 * @return データ抽出結果
	 */
	private List<Z101TblSyokaiBean> readData(String mode, PrintWriter outCsv) throws Exception {
		
		//データベースを読み書きする準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		//検索結果格納リストの定義・初期化
		List<Z101TblSyokaiBean> list = new ArrayList<Z101TblSyokaiBean>();
		list.clear();
		
		//検索SQLの作成
		String sql = "";
		String strAnd = "";
		
		if (AtgUtils.checkMust(clmnm) || AtgUtils.checkMust(clmjnm)) {
			//項目名の指定があり、テーブル項目マスタをJOINする場合
			sql += "SELECT A.TBLNM,A.TBLJNM,A.SYS,A.SHU,B.BNM,B.RNM,B.KATA,LNG1,LNG2";
			sql += " FROM ZM_TBL A, ZM_TBLITM B ";
			sql += " WHERE A.KC='"+kc+"' AND B.KC='"+kc+"' AND A.TBLNM = B.TBLNM ";
			strAnd = "AND";
			if (AtgUtils.checkMust(clmnm)) {
				//カラム名が入力されている場合、検索条件を追加
				if (strAnd.equals("")) {
					sql += " WHERE ";
				}
				sql += strAnd + " B.BNM LIKE '%"+clmnm+"%' ";
				strAnd = "AND";
			}
			if (AtgUtils.checkMust(clmjnm)) {
				//カラム名称入力されている場合、検索条件を追加
				if (strAnd.equals("")) {
					sql += " WHERE ";
				}
				sql += strAnd + " B.RNM LIKE '%"+clmjnm+"%' ";
				strAnd = "AND";
			}
			clmList = true;
		} else {
			//項目名の指定が無く、テーブル項目マスタをJOINしない場合
			sql += "SELECT A.TBLNM,A.TBLJNM,A.SYS,A.SHU FROM ZM_TBL A WHERE A.KC='"+kc+"' ";
			strAnd = "AND";
			clmList = false;
		}
		
		if (AtgUtils.checkMust(sys)) {
			//システム区分が入力されている場合、検索条件を追加
			if (strAnd.equals("")) {
				sql += " WHERE ";
			}
			sql += strAnd + " A.SYS = '"+sys+"' ";
			strAnd = "AND";
		}
		if (AtgUtils.checkMust(shu)) {
			//種別が入力されている場合、検索条件を追加
			if (strAnd.equals("")) {
				sql += " WHERE ";
			}
			sql += strAnd + " A.SHU = '"+shu+"' ";
			strAnd = "AND";
		}
		if (AtgUtils.checkMust(tblnm)) {
			//テーブル名が入力されている場合、検索条件を追加
			if (strAnd.equals("")) {
				sql += " WHERE ";
			}
			sql += strAnd + " A.TBLNM LIKE '%"+tblnm+"%' ";
			strAnd = "AND";
		}
		if (AtgUtils.checkMust(tbljnm)) {
			//テーブル日本語名が入力されている場合、検索条件を追加
			if (strAnd.equals("")) {
				sql += " WHERE ";
			}
			sql += strAnd + " A.TBLJNM LIKE '%"+tbljnm+"%' ";
			strAnd = "AND";
		}
		if (AtgUtils.checkMust(clmnm) || AtgUtils.checkMust(clmjnm)) {
			//項目名の指定があり、テーブル項目マスタをJOINする場合
			sql += " ORDER BY A.TBLNM,B.BNM";
		} else {
			//項目名の指定が無く、テーブル項目マスタをJOINしない場合
			sql += " ORDER BY A.TBLNM";
		}
		
		if (!mode.equals("csv")) {
			sql += " LIMIT "+getPageCnt()*getPageSize()+","+((getPageCnt()+1)*getPageSize()+1);
		}
		
		//SQLのセット・検索の実行
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		
		//検索ページサイズ・最大サイズの指定
		int max = getPageSize();
		if (mode.equals("csv")) {
			//CSV出力の場合、全件抽出する
			max = Integer.MAX_VALUE;
		} else {
			//画面リストクリア
			Z101TblSyokaiList.clear();
		}
		
		//件数カウンタ
		int linecnt = 0;
		
		//コードマスタ取得用・共通クラス
		SubCode sc = new SubCode();
		
		//次頁有りフラグ
		boolean nextFlg = false;
		
		//検索結果を検索結果格納リストに入れる
		while(rs.next() && nextFlg == false) {
			if (max > linecnt) {
				//画面明細数までは、画面ワークエリアに格納
				//画面明細を定義
				Z101TblSyokaiBean setCol = new Z101TblSyokaiBean();
				//画面明細への格納
				//システム区分
				setCol.setSys(rs.get("A.SYS").toString());
				//種別
				setCol.setShu(rs.get("A.SHU").toString());
				//テーブル名
				setCol.setTblnm(rs.get("A.TBLNM").toString());
				//テーブル日本語名
				setCol.setTbljnm(rs.get("A.TBLJNM").toString());
				//システム名称
				sc.readCode(dao, kc, "001", setCol.getSys());
				setCol.setSysmei(sc.getCdnm());
				//種別名称
				sc.readCode(dao, kc, "002", setCol.getShu());
				setCol.setShumei(sc.getCdnm());
				//カラム名・カラム名称
				if (AtgUtils.checkMust(clmnm) || AtgUtils.checkMust(clmjnm)) {
					//項目名の指定がある場合
					//カラム名
					setCol.setClmnm(rs.get("B.BNM").toString());
					//カラム名称
					setCol.setClmjnm(rs.get("B.RNM").toString());
					//型
					setCol.setKata(rs.get("B.KATA").toString());
					//長さ１
					setCol.setLng1(rs.get("B.LNG1").toString());
					//長さ２
					setCol.setLng2(rs.get("B.LNG2").toString());
				} else {
					//項目名の指定が無い場合
					//カラム名
					setCol.setClmnm("");
					//カラム名称
					setCol.setClmjnm("");
					//型
					setCol.setKata("");
					//長さ１
					setCol.setLng1("");
					//長さ２
					setCol.setLng2("");
				}
				if (!mode.equals("csv")) {
					//組み立てたデータを検索結果格納リストへ入れる
					list.add(setCol);
				} else {
					//組み立てたデータをCSVへ出力する
					outCsv.print(setCol.getSys()+",");
					outCsv.print(setCol.getSysmei()+",");
					outCsv.print(setCol.getShu()+",");
					outCsv.print(setCol.getShumei()+",");
					outCsv.print(setCol.getTblnm()+",");
					outCsv.print(setCol.getTbljnm()+",");
					if (AtgUtils.checkMust(clmnm) || AtgUtils.checkMust(clmjnm)) {
						//項目名の指定がある場合
						//カラム名
						outCsv.print(setCol.getClmnm()+",");
						//カラム名称
						outCsv.print(setCol.getClmjnm()+",");
						//型
						outCsv.print(setCol.getKata()+",");
						//長さ１
						outCsv.print(setCol.getLng1()+",");
						//長さ２
						outCsv.print(setCol.getLng2()+",");
					}
					outCsv.print(System.getProperty("line.separator"));
				    outCsv.flush();
				}
			} else {
				//画面明細数を超えたら、nextFlgをtrueにセットし、ループ終了
				nextFlg = true;
			}
			//１行出力したらカウント
			linecnt++;
		}
		
		if (!mode.equals("csv")) {
			if (getPageCnt() < getOrgPage()) {
				//前頁取得の場合、次頁有設定
				setHasMore(true);
			} else {
				if (nextFlg) {
					//画面リスト以上のレコードが取得できた場合、次頁有設定
					setHasMore(true);
				} else {
					//画面リスト以上のレコードが取得できなかった場合、次頁なし設定
					setHasMore(false);
				}
			}
		}
		
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
		return list;
	}
	
	/**
	 * メニューへ戻る
	 * @throws Exception 
	 */
	@Override
	protected String subCancel() {
		return getCallOrigin();
	}

	/**
	 * CSV出力処理
	 * <p>
	 * 検索処理を行い、CSVを出力します<br>
	 * 
	 * @param 	outCsv PrintWriter CSVデータスプールオブジェクト
	 * @throws Exception 例外
	 */
	@Override
	public void subCsvDownload(PrintWriter outCsv) throws Exception {
		
		int no=0;
		String csvtbl="";
		
		if (LafitUtils.checkMust(csvKbn) && LafitUtils.checkMust(getColumNo())) {
			if (csvKbn.equals("csv1") || csvKbn.equals("csv3") || csvKbn.equals("csv4")) {
				no = Integer.parseInt(getColumNo());
				csvtbl = Z101TblSyokaiList.get(no).getTblnm();
//				log.info("選択カラムNo:"+no+","+Z101TblSyokaiList.get(no).getTblnm());
			}
		}
		
		if (csvKbn.equals("csv1")) {
			//CSV出力用オブジェクトを初期化し、取得する
			//ファイル名を指定して作成
		    outCsv = getCsvWriter(csvtbl+".csv");
	        outCsv.flush() ;

	        //ヘッダ情報出力(1行目:テーブル日本語名)
	        outCsv.print(Z101TblSyokaiList.get(no).getTbljnm());
			outCsv.print(System.getProperty("line.separator"));
	        outCsv.flush() ;
	        
	        //ヘッダ情報出力(2行目:テーブル物理名)
	        outCsv.print(csvtbl);
			outCsv.print(System.getProperty("line.separator"));
	        outCsv.flush() ;
	        
	        //ヘッダ情報出力(3行目:見出し)
	        outCsv.print("項目名,項目ID,型,長さ1,長さ2,必須区分(1),デフォルト値,一意区分(U),備考");
			outCsv.print(System.getProperty("line.separator"));
	        outCsv.flush() ;
	        
	        //csv作成
	        makeTableCsv(outCsv, csvtbl);
	        
	        
		} else if (csvKbn.equals("csv3")) {
			
			if (getLoginBean().getKc().equals("85")) {
				
				//CSV出力用オブジェクトを初期化し、取得する
				//ファイル名を指定して作成
			    outCsv = getCsvWriter(csvtbl+"_CRE.sql");
		        outCsv.flush() ;

		        //ヘッダ情報出力(1行目:見出し)
		        outCsv.print("--"+Z101TblSyokaiList.get(no).getTbljnm());
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //ヘッダ情報出力(2行目:drop文)
		        outCsv.print("drop table "+csvtbl+"");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("//");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //ヘッダ情報出力(3行目:drop文)
		        outCsv.print("drop index "+csvtbl+"_pk");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("//");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        outCsv.print("");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //ヘッダ情報出力(3行目～:create文)
		        outCsv.print("create table "+csvtbl+"(");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //csv作成
		        uniqueKey = "";
		        makeTableSql_Lafit(outCsv, csvtbl);
		        
		        outCsv.print(" ,primary key("+csvtbl+"ID))");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("//");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("DROP SEQUENCE "+csvtbl+"_ID_SEQ");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("//");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("CREATE SEQUENCE MAXUSER."+csvtbl+"_ID_SEQ");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print(" INCREMENT BY 1 START WITH 1");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print(" MINVALUE 1 MAXVALUE 99999999999999999999999999999999999999 NOCYCLE CACHE 1");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("//");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("create unique index "+csvtbl+"_pk on");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("  MAXUSER."+csvtbl+"("+uniqueKey+")");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("//");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
			} else {
				//CSV出力用オブジェクトを初期化し、取得する
				//ファイル名を指定して作成
			    outCsv = getCsvWriter(csvtbl+"_CRE.sql");
		        outCsv.flush() ;

		        //ヘッダ情報出力(1行目:見出し)
		        outCsv.print("-- "+Z101TblSyokaiList.get(no).getTbljnm());
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //ヘッダ情報出力(2行目:drop文)
		        outCsv.print("drop table "+csvtbl+";");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //ヘッダ情報出力(3行目～:create文)
		        outCsv.print("create table "+csvtbl+" (");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        //csv作成
		        makeTableSql(outCsv, csvtbl);
		        
		        outCsv.print(") CHARACTER SET utf8;");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
		        
		        outCsv.print("alter table "+csvtbl+" engine = InnoDB;");
				outCsv.print(System.getProperty("line.separator"));
		        outCsv.flush() ;
			}
		        
		} else if (csvKbn.equals("csv2")) {
			//CSV出力用オブジェクトを初期化し、取得する
			//ファイル名を指定して作成
		    outCsv = getCsvWriter("table_list.csv");
	        outCsv.flush() ;

	        //ヘッダ情報出力(1行目)
	        outCsv.print("テーブル一覧");
			outCsv.print(System.getProperty("line.separator"));
	        outCsv.flush() ;
			outCsv.print(System.getProperty("line.separator"));
	        outCsv.flush() ;

			//ヘッダー情報出力(明細タイトル)
			if (AtgUtils.checkMust(clmnm) || AtgUtils.checkMust(clmjnm)) {
				//項目名の指定がある場合
				outCsv.print("システム区分,システム区分名,種別区分,種別区分名,");
				outCsv.print("テーブル名,テーブル名称,項目名,項目名称,型,長さ１,長さ２");
			} else {
				//項目名の指定がない場合
				outCsv.print("システム区分,システム区分名,種別区分,種別区分名,テーブル名,テーブル名称");
			}
			outCsv.print(System.getProperty("line.separator"));
	        outCsv.flush() ;
	        
	        //明細の読み込みとCSVデータの出力
	        readData("csv", outCsv);
	        
		} else if (csvKbn.equals("csv4")) {
			//CSV出力用オブジェクトを初期化し、取得する
			//ファイル名を指定して作成
//		    outCsv = getCsvWriterUtf8(csvtbl.toUpperCase().substring(0,1)+csvtbl.toLowerCase().substring(1,2)+"_"+csvtbl.toUpperCase().substring(3,4)+csvtbl.toLowerCase().substring(4)+"Tool.java");
			
			if (getLoginBean().getKc().equals("85")) {
			    outCsv = getCsvWriter(csvtbl.toUpperCase()+"_tool.java");
			} else {
			    outCsv = getCsvWriterUtf8(csvtbl.toUpperCase()+"_tool.java");
			}
	        outCsv.flush() ;

	        //明細の読み込みとCSVデータの出力
//	        makeJavaCode(outCsv, csvtbl);
	        
        	String sSQL = "";
        	sSQL += " SELECT TBLNM ";
        	sSQL += " FROM ZM_TBL";
        	
        	Condition cond = new Condition();
        	cond.setSQL(sSQL);
        	RecordSet rs = dao.select(cond);
        	cond.clear();
        	
        	while(rs.next()){
//        		log.info(rs.getStringValue("TBLNM"));
        		makeJavaCode2(rs.getStringValue("TBLNM"));
        	}
		}
		
	}

	/**
	 * テーブルSQL作成
	 * 
	 * @param outCsv csvライター
	 * @throws Exception
	 */
	private void makeTableSql(PrintWriter outCsv, String csvtbl) throws Exception {
		
		//データベースを読み書きする準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		String pkey = "";
		
		//検索SQLの作成
		String sql = "";
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		sql += " ORDER BY TBLNM,TBLNO";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		int linecnt = 0;
		
		while (rs.next()) {
			
			String rnm = rs.getStringValue("RNM");
			String bnm = rs.getStringValue("BNM");
			String kata = rs.getStringValue("KATA");
			String lng1 = rs.getStringValue("LNG1");
			String lng2 = rs.getStringValue("LNG2");
			String hsu = rs.getStringValue("HSU");
			String dflt = rs.getStringValue("DFLT");
			String tkey1 = rs.getStringValue("TKEY01");
			
			String str = " ";
			if (linecnt != 0) {
				str = ",";
			}
			
			str += bnm+" ";
			if (kata.equals("9")) {
				str += "NUMERIC ("+lng1+","+lng2+") ";
			} else if (kata.equals("X")) {
				str += "CHAR ("+lng1+") ";
			} else if (kata.equals("V")) {
				str += "VARCHAR ("+lng1+") ";
			} else if (kata.equals("T")) {
				str += "DATETIME ";
			}
			
			if (hsu.equals("1")) {
				str += "NOT NULL ";
			} else {
				if (dflt.equals("SPACE")) {
					str += "DEFAULT ' ' ";
				} else if (dflt.equals("0")) {
					str += "DEFAULT 0 ";
				} else if (kata.equals("9")) {
					str += "DEFAULT 0 ";
				} else if (kata.equals("T")) {
					str += " ";
				} else {
					str += "DEFAULT ' ' ";
				}
			}
			
			str += "COMMENT '"+rnm+"'";
			
			if (tkey1.equals("U")) {
				if (LafitUtils.checkMust(pkey)) {
					pkey += ","+bnm;
				} else {
					pkey += bnm;
				}
			}
			
			outCsv.print(str);
			outCsv.print(System.getProperty("line.separator"));
		    outCsv.flush();
		    
		    linecnt++;
		}
		
		if (AtgUtils.checkMust(pkey)) {
			outCsv.print(",primary key("+pkey+")");
			outCsv.print(System.getProperty("line.separator"));
		    outCsv.flush();
		}
		
//		log.info(linecnt+"行出力しました");
		
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
	}
	
	/**
	 * テーブルSQL_LAFIT作成
	 * 
	 * @param outCsv csvライター
	 * @throws Exception
	 */
	private void makeTableSql_Lafit(PrintWriter outCsv, String csvtbl) throws Exception {
		
		//データベースを読み書きする準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		String pkey = "";
		
		//検索SQLの作成
		String sql = "";
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		sql += " ORDER BY TBLNM,TBLNO";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		int linecnt = 0;
		
		while (rs.next()) {
			
//			String rnm = rs.getStringValue("RNM");
			String bnm = rs.getStringValue("BNM");
			String kata = rs.getStringValue("KATA");
			String lng1 = rs.getStringValue("LNG1");
			String lng2 = rs.getStringValue("LNG2");
			String hsu = rs.getStringValue("HSU");
			String dflt = rs.getStringValue("DFLT");
			String tkey1 = rs.getStringValue("TKEY01");
			
			String str = "  ";
			if (linecnt != 0) {
				str = " ,";
			}
			
			str += bnm+" ";
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					str += "fixed("+lng1+") ";
				} else {
					str += "fixed("+lng1+","+lng2+") ";
				}
			} else if (kata.equals("X")) {
				str += "char("+lng1+")UNICODE ";
			} else if (kata.equals("V")) {
				str += "varchar("+lng1+")UNICODE ";
			} else if (kata.equals("T")) {
				str += "timestamp ";
			}
			
			if (hsu.equals("1")) {
				str += "NOT NULL ";
			} else {
				if (dflt.equals("SPACE")) {
					str += "default ' ' ";
				} else if (dflt.equals("0")) {
					str += "default 0 ";
				} else if (kata.equals("9")) {
					str += "default 0 ";
				} else if (kata.equals("T")) {
					str += " ";
				} else {
					str += "default ' ' ";
				}
			}
			
			if (tkey1.equals("U")) {
				if (LafitUtils.checkMust(pkey)) {
					pkey += ","+bnm;
				} else {
					pkey += bnm;
				}
			}
			
			outCsv.print(str);
			outCsv.print(System.getProperty("line.separator"));
		    outCsv.flush();
		    
		    linecnt++;
		}
		
		uniqueKey = pkey;
		
//		log.info(linecnt+"行出力しました");
		
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
	}
	
	/**
	 * JavaCode作成
	 * 
	 * @param outCsv csvライター
	 * @throws Exception
	 */
	private void makeJavaCode(PrintWriter outCsv, String csvtbl) throws Exception {
		
		//データベースを読み書きする準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		String tblnm="";
		String tbljnm="";
		String where="";
		String uke="";
		
		String utilsname = "LafitUtils";
		
		if (getLoginBean().getKc().equals("81")) {
			utilsname = "AtgUtils";
		}
		
		boolean timestamp = false;
		
		//検索SQLの作成
		String sql = "";
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLJNM";
		sql += " FROM ZM_TBL";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		if (rs.next()) {
			tbljnm = rs.getStringValue("TBLJNM");
		}
		rs.close();
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		sql += " ORDER BY TBLNM,TBLNO";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		int linecnt = 0;
		
		ArrayList<String> teigi = new ArrayList<String>();
		ArrayList<String> shokika = new ArrayList<String>();
		ArrayList<String> koumoku = new ArrayList<String>();
		ArrayList<String> koumoku2 = new ArrayList<String>();
		ArrayList<String> ari = new ArrayList<String>();
		ArrayList<String> nasi = new ArrayList<String>();
		ArrayList<String> tter = new ArrayList<String>();
		ArrayList<String> param = new ArrayList<String>();
		//▽2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		ArrayList<String> setlist = new ArrayList<String>();
		//△2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		
		while (rs.next()) {
			
			String rnm = rs.getStringValue("RNM");
			String bnm = rs.getStringValue("BNM");
			String kata = rs.getStringValue("KATA");
			String lng1 = rs.getStringValue("LNG1");
			String lng2 = rs.getStringValue("LNG2");
			String tkey1 = rs.getStringValue("TKEY01");
			tblnm = rs.getStringValue("TBLNM");
			
			//頭1文字が大文字のBnm
			String headUpperBnm = bnm.toUpperCase().substring(0,1)+bnm.toLowerCase().substring(1);
			
			//定義句の作成
			teigi.add("\t/**");
			teigi.add("\t * "+rnm);
			if (kata.equals("9")) {
				teigi.add("\t * "+bnm+" "+kata+"("+lng1+","+lng2+")");
			} else if (kata.equals("T")) {
				teigi.add("\t * "+bnm+" "+kata);
			} else {
				teigi.add("\t * "+bnm+" "+kata+"("+lng1+")");
			}
			teigi.add("\t */");
			
			//▽2020.09.02 sakurai 予約語対応
//			if (kata.equals("9")) {
//				if (lng2.equals("0")) {
//					teigi.add("\tprivate Long "+bnm.toLowerCase()+";");
//				} else {
//					teigi.add("\tprivate Double "+bnm.toLowerCase()+";");
//				}
//			} else if (kata.equals("X") || kata.equals("V")) {
//				teigi.add("\tprivate String "+bnm.toLowerCase()+";");
//			} else if (kata.equals("T")) {
//				teigi.add("\tprivate Timestamp "+bnm.toLowerCase()+";");
//			}
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					if(checkJavaYoyaku(bnm)){
						teigi.add("\tprivate Long "+bnm.toLowerCase()+"Yoyaku;");
					}else{						
						teigi.add("\tprivate Long "+bnm.toLowerCase()+";");
					}
				} else {
					if(checkJavaYoyaku(bnm)){
						teigi.add("\tprivate Double "+bnm.toLowerCase()+"Yoyaku;");
					}else{						
						teigi.add("\tprivate Double "+bnm.toLowerCase()+";");
					}
				}
			} else if (kata.equals("X") || kata.equals("V")) {
				if(checkJavaYoyaku(bnm)){
					teigi.add("\tprivate String "+bnm.toLowerCase()+"Yoyaku;");
				}else{					
					teigi.add("\tprivate String "+bnm.toLowerCase()+";");
				}
			} else if (kata.equals("T")) {
				if(checkJavaYoyaku(bnm)){
					teigi.add("\tprivate Timestamp "+bnm.toLowerCase()+"Yoyaku;");
				}else{					
					teigi.add("\tprivate Timestamp "+bnm.toLowerCase()+";");
				}
			}
			//△2020.09.02 sakurai 予約語対応
			teigi.add("");
			
			//初期化句の作成
			//▽2020.09.02 sakurai 予約語対応
//			if (kata.equals("9")) {
//				if (lng2.equals("0")) {
//					shokika.add("\t\t"+bnm.toLowerCase()+"=0L;\t// "+rnm);
//				} else {
//					shokika.add("\t\t"+bnm.toLowerCase()+"=0D;\t// "+rnm);
//				}
//			} else if (kata.equals("T")) {
//				shokika.add("\t\t"+bnm.toLowerCase()+"=null;\t// "+rnm);
//			} else {
//				shokika.add("\t\t"+bnm.toLowerCase()+"=\"\";\t// "+rnm);
//			}
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					if(checkJavaYoyaku(bnm)){
						shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=0L;\t// "+rnm);
					}else{						
						shokika.add("\t\t"+bnm.toLowerCase()+"=0L;\t// "+rnm);
					}
				} else {
					if(checkJavaYoyaku(bnm)){
						shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=0D;\t// "+rnm);
					}else{						
						shokika.add("\t\t"+bnm.toLowerCase()+"=0D;\t// "+rnm);
					}
				}
			} else if (kata.equals("T")) {
				if(checkJavaYoyaku(bnm)){
					shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=null;\t// "+rnm);
				}else{					
					shokika.add("\t\t"+bnm.toLowerCase()+"=null;\t// "+rnm);
				}
			} else {
				if(checkJavaYoyaku(bnm)){
					shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=\"\";\t// "+rnm);
				}else{					
					shokika.add("\t\t"+bnm.toLowerCase()+"=\"\";\t// "+rnm);
				}
			}
			//△2020.09.02 sakurai 予約語対応
			
			//項目句の作成
			//▽2020.09.02 sakurai 予約語対応
//			if (linecnt == 0) {
//				koumoku.add("\t "+bnm.toUpperCase());
//			} else {
//				koumoku.add("\t,"+bnm.toUpperCase());
//			}
			if (linecnt == 0) {
				if(checkOracleYoyaku(bnm)){
					koumoku.add("\t \\\""+bnm.toUpperCase()+"\\\"");
				}else{					
					koumoku.add("\t "+bnm.toUpperCase());
				}
			} else {
				if(checkOracleYoyaku(bnm)){
					koumoku.add("\t,\\\""+bnm.toUpperCase()+"\\\"");
				}else{
					koumoku.add("\t,"+bnm.toUpperCase());					
				}
			}
			//△2020.09.02 sakurai 予約語対応
			koumoku2.add(bnm.toUpperCase());
			
			//存在した場合の代入句
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					//▽2020.08.28 sakurai im移行
//					ari.add("\t"+bnm.toLowerCase()+"=CommonUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
					//▽2020.09.02 sakurai 予約語対応
//					ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
					if(checkOracleYoyaku(bnm)){
						ari.add("\tif(rs.get(\"\\\""+bnm+"\\\"\") != null){");
						if(checkJavaYoyaku(bnm)){
							ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getLongValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");							
						}else{
							ari.add("\t\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");
						}
					}else{
						ari.add("\tif(rs.get(\""+bnm+"\") != null){");
						if(checkJavaYoyaku(bnm)){
							ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");							
						}else{
							ari.add("\t\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
						}
					}
					ari.add("\t}else{");
					ari.add("\t\t"+bnm.toLowerCase()+" = null");
					ari.add("\t}");
					//△2020.09.02 sakurai 予約語対応
					//△2020.08.28 sakurai im移行
				} else {
					//▽2020.08.28 sakurai im移行
//					ari.add("\t"+bnm.toLowerCase()+"=CommonUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");
					//▽2020.09.02 sakurai 予約語対応
//					ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");
					if(checkOracleYoyaku(bnm)){
						if(checkJavaYoyaku(bnm)){
							ari.add("\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getDoubleValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");							
						}else{
							ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getDoubleValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");
						}
					}else{
						if(checkJavaYoyaku(bnm)){
							ari.add("\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");							
						}else{
							ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");
						}
					}
					//△2020.09.02 sakurai 予約語対応
					//△2020.08.28 sakurai im移行
				}
			} else if (kata.equals("T")) {
				//▽2020.09.02 sakurai 予約語対応
//				ari.add("\t"+bnm.toLowerCase()+"=Timestamp.valueOf(rs.get(\""+bnm+"\").toString());");
				if(checkOracleYoyaku(bnm)){
					if(checkJavaYoyaku(bnm)){						
						ari.add("\t"+bnm.toLowerCase()+"Yoyaku=Timestamp.valueOf(rs.get(\"\\\""+bnm+"\\\"\").toString());");
					}else{
						ari.add("\t"+bnm.toLowerCase()+"=Timestamp.valueOf(rs.get(\"\\\""+bnm+"\\\"\").toString());");
					}
				}else{
					if(checkJavaYoyaku(bnm)){
						ari.add("\t"+bnm.toLowerCase()+"Yoyaku=Timestamp.valueOf(rs.get(\""+bnm+"\").toString());");
					}else{
						ari.add("\t"+bnm.toLowerCase()+"=Timestamp.valueOf(rs.get(\""+bnm+"\").toString());");
					}
				}
				//△2020.09.02 sakurai 予約語対応
				timestamp = true;
			} else {
				//▽2020.09.02 sakurai 予約語対応
//				ari.add("\t"+bnm.toLowerCase()+"=rs.getStringValue(\""+bnm+"\");");
				if(checkOracleYoyaku(bnm)){
					if(checkJavaYoyaku(bnm)){						
						ari.add("\t"+bnm.toLowerCase()+"Yoyaku=rs.getStringValue(\"\\\""+bnm+"\\\"\");");
					}else{
						ari.add("\t"+bnm.toLowerCase()+"=rs.getStringValue(\"\\\""+bnm+"\\\"\");");
					}
				}else{
					if(checkJavaYoyaku(bnm)){
						ari.add("\t"+bnm.toLowerCase()+"Yoyaku=rs.getStringValue(\""+bnm+"\");");
					}else{						
						ari.add("\t"+bnm.toLowerCase()+"=rs.getStringValue(\""+bnm+"\");");
					}
				}
				//△2020.09.02 sakurai 予約語対応
			}
			
			//存在しない場合の代入句
			//▽2020.09.02 sakurai 予約語対応
//			nasi.add("\t"+bnm.toLowerCase()+"=null;");
			if(checkJavaYoyaku(bnm)){
				nasi.add("\t"+bnm.toLowerCase()+"Yoyaku=null;");
			}else{
				nasi.add("\t"+bnm.toLowerCase()+"=null;");
			}
			//△2020.09.02 sakurai 予約語対応
			//Getter,Setter
			tter.add("\t/**");
			tter.add("\t * "+rnm+"を取得します。<br>");
			if (kata.equals("9")) {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+","+lng2+")");
			} else if (kata.equals("T")) {
				tter.add("\t * "+bnm+" "+kata);
			} else {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+")");
			}
			tter.add("\t * @return "+rnm);
			tter.add("\t */");
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic Long get"+headUpperBnm+"() {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic Long get"+headUpperBnm+"Yoyaku() {");
					}else{						
						tter.add("\tpublic Long get"+headUpperBnm+"() {");
					}
					//△2020.09.02 sakurai 予約語対応
				} else {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic Double get"+headUpperBnm+"() {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic Double get"+headUpperBnm+"Yoyaku() {");
					}else{						
						tter.add("\tpublic Double get"+headUpperBnm+"() {");
					}
					//△2020.09.02 sakurai 予約語対応
				}
			} else if (kata.equals("T")) {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic Timestamp get"+headUpperBnm+"() {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic Timestamp get"+headUpperBnm+"Yoyaku() {");
				}else{						
					tter.add("\tpublic Timestamp get"+headUpperBnm+"() {");
				}
				//△2020.09.02 sakurai 予約語対応
			} else {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic String get"+headUpperBnm+"() {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic String get"+headUpperBnm+"Yoyaku() {");
				}else{						
					tter.add("\tpublic String get"+headUpperBnm+"() {");
				}
				//△2020.09.02 sakurai 予約語対応
			}
			//▽2020.09.02 sakurai 予約語対応
//			tter.add("\t\treturn "+bnm.toLowerCase()+";");
			if(checkJavaYoyaku(bnm)){
				tter.add("\t\treturn "+bnm.toLowerCase()+"Yoyaku;");
			}else{
				tter.add("\t\treturn "+bnm.toLowerCase()+";");
			}
			//△2020.09.02 sakurai 予約語対応
			tter.add("\t}");
			tter.add("");
			
			tter.add("\t/**");
			tter.add("\t * "+rnm+"を設定します。<br>");
			if (kata.equals("9")) {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+","+lng2+")");
			} else if (kata.equals("T")) {
				tter.add("\t * "+bnm+" "+kata);
			} else {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+")");
			}
			tter.add("\t * @param "+rnm);
			tter.add("\t */");
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic void set"+headUpperBnm+"(Long "+bnm.toLowerCase()+") {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(Long "+bnm.toLowerCase()+"Yoyaku) {");
					}else{
						tter.add("\tpublic void set"+headUpperBnm+"(Long "+bnm.toLowerCase()+") {");
					}
					//△2020.09.02 sakurai 予約語対応
				} else {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic void set"+headUpperBnm+"(Double "+bnm.toLowerCase()+") {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(Double "+bnm.toLowerCase()+"Yoyaku) {");
					}else{
						tter.add("\tpublic void set"+headUpperBnm+"(Double "+bnm.toLowerCase()+") {");
					}
					//△2020.09.02 sakurai 予約語対応
				}
			} else if (kata.equals("T")) {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic void set"+headUpperBnm+"(Timestamp "+bnm.toLowerCase()+") {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(Timestamp "+bnm.toLowerCase()+"Yoyaku) {");
				}else{
					tter.add("\tpublic void set"+headUpperBnm+"(Timestamp "+bnm.toLowerCase()+") {");
				}
				//△2020.09.02 sakurai 予約語対応
			} else {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic void set"+headUpperBnm+"(String "+bnm.toLowerCase()+") {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(String "+bnm.toLowerCase()+"Yoyaku) {");
				}else{
					tter.add("\tpublic void set"+headUpperBnm+"(String "+bnm.toLowerCase()+") {");
				}
				//△2020.09.02 sakurai 予約語対応
			}
			//▽2020.09.02 sakurai 予約語対応
//			tter.add("\t\tthis."+bnm.toLowerCase()+"="+bnm.toLowerCase()+";");
			if(checkJavaYoyaku(bnm)){
				tter.add("\t\tthis."+bnm.toLowerCase()+"Yoyaku="+bnm.toLowerCase()+"Yoyaku;");
			}else{
				tter.add("\t\tthis."+bnm.toLowerCase()+"="+bnm.toLowerCase()+";");
			}
			//△2020.09.02 sakurai 予約語対応
			tter.add("\t}");
			tter.add("");
			
			//主キーから取得
			if (tkey1.equals("U")) {
				
				// WHERE句を作成
				if (!where.equals("")) {
					where +=" AND ";
				}
				if (kata.equals("9")) {
					where += bnm+"=\"+in"+headUpperBnm+"+\"";
				} else {
					where += bnm+"='\"+in"+headUpperBnm+"+\"'";
				}
				
				// readXXX()内を作成
				if (kata.equals("9")) {
					if (lng2.equals("0")) {
						uke += ",Long in"+headUpperBnm;
					} else {
						uke += ",Double in"+headUpperBnm;
					}
				} else if (kata.equals("T")) {
					uke += ",Timestamp in"+headUpperBnm;
				} else {
					uke += ",String in"+headUpperBnm;
				}
				
				// メソッドコメントを作成
				param.add("\t * @param in"+headUpperBnm+" "+rnm);
				
			}
			
			//▽2020.09.18 sakurai setメソッド追加 
			if(checkJavaYoyaku(bnm)){
				setlist.add("\t\ttmpTool.set"+headUpperBnm+"Yoyaku(inTool.get"+headUpperBnm+"Yoyaku());");
			}else{
				setlist.add("\t\ttmpTool.set"+headUpperBnm+"(inTool.get"+headUpperBnm+"());");
			}
			//△2020.09.18 sakurai setメソッド追加 
			
			linecnt++;
		}
		
		//順番にJavaコードを組み立てる
		
		//メソッド名称の設定
		String methodName = csvtbl.toUpperCase();
//		String methodName = csvtbl.toUpperCase().substring(0,1)+csvtbl.toUpperCase().substring(1,2)+"_"+csvtbl.toUpperCase().substring(3,4)+csvtbl.toUpperCase().substring(4);
		
		if (utilsname.equals("AtgUtils")) {
			outline(outCsv,"package jp.co.tisa.atg.tools;");
		} else {
			//▽2020.7.14 sakurai im移行
//			outline(outCsv,"package jp.co.tisa.lafit.tools;");
			outline(outCsv,"package jp.co.tisa.lafit.bl.entity_tool;");
			//△2020.7.14 sakurai im移行
		}
		outline(outCsv,"");
		
		if (timestamp == true) {
			outline(outCsv,"import java.sql.Timestamp;");
			outline(outCsv,"");
		}
		
		if (utilsname.equals("AtgUtils")) {
			outline(outCsv,"import jp.co.tisa.atg.base.BusinessBeanBase;");
			outline(outCsv,"import jp.co.tisa.atg.base.common.util.function.AtgUtils;");
			outline(outCsv,"import jp.co.tisa.atg.base.common.util.function.CommonUtils;");
			outline(outCsv,"import jp.co.tisa.atg.base.dao.Condition;");
			outline(outCsv,"import jp.co.tisa.atg.base.dao.Dao;");
			outline(outCsv,"import jp.co.tisa.atg.base.dao.RecordSet;");
		} else {
			//▽2020.7.14 sakurai im移行
//			outline(outCsv,"import jp.co.tisa.lafit.base.BusinessBeanBase;");
//			outline(outCsv,"import jp.co.tisa.lafit.base.dao.Dao;");
//			outline(outCsv,"import jp.co.tisa.lafit.base.dao.Condition;");
//			outline(outCsv,"import jp.co.tisa.lafit.base.dao.RecordSet;");
//			outline(outCsv,"import jp.co.tisa.lafit.base.common.util.function.CommonUtils;");
			outline(outCsv,"import jp.co.tisa.lafit.framework.base.BlBase;");
			outline(outCsv,"import jp.co.tisa.lafit.framework.dao.Dao;");
			outline(outCsv,"import jp.co.tisa.lafit.framework.dao.Condition;");
			outline(outCsv,"import jp.co.tisa.lafit.framework.dao.RecordSet;");
			//▽2020.09.03 sakurai util削除対応
//			outline(outCsv,"import jp.co.tisa.lafit.framework.util.LafitUtils;");
			outline(outCsv,"import jp.co.tisa.lafit.framework.utils.LafitUtils;");
			//△2020.09.03 sakurai util削除対応
			//△2020.7.14 sakurai im移行
		}
		
		outline(outCsv,"");
		outline(outCsv,"/**");
		outline(outCsv," * "+tbljnm+"("+tblnm+")取得");
		outline(outCsv," * <p>");
		outline(outCsv," * @author 自動生成");
		outline(outCsv," */");
		//▽2020.7.14 sakurai im移行
//		outline(outCsv,"public class "+methodName+"_tool extends BusinessBeanBase {");
//		outline(outCsv,"@SuppressWarnings(\"serial\")");
		outline(outCsv,"public class "+methodName+"_tool extends BlBase {");
		//△2020.7.14 sakurai im移行
		outline(outCsv,"");
		
		//定義の作成
		
		for(String str:teigi) {
			outline(outCsv,str);
		}
		
		outline(outCsv,"\tRecordSet rs = null;");
		outline(outCsv,"\tCondition cond = new Condition();");
		outline(outCsv,"");

		//コンストラクタの作成
		
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * コンストラクタ");
		outline(outCsv,"\t * <p>");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		outline(outCsv,"\tpublic "+methodName+"_tool() throws Exception {");
		outline(outCsv,"\t\tsuper();");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		
		
		
		
		
		//list句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")のレコードセットの作成");
		outline(outCsv,"\t * @param dao");
		outline(outCsv,"\t * @param where where句");
		outline(outCsv,"\t * @param order order句");
		//▽2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
//		outline(outCsv,"\t * @param limit limit句");
		//△2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		String list = "\tpublic void list_"+methodName;
		//▽2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
//		list += "(Dao dao, String where, String order, String limit) throws Exception {";
		list += "(Dao dao, String where, String order) throws Exception {";
		//△2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
		outline(outCsv,list);
		outline(outCsv,"");
		
		outline(outCsv,"\t\tinit();");
		outline(outCsv,"");
		outline(outCsv,"\t\tString sSQL = \"SELECT \";");
		for(String str:koumoku) {
			outline(outCsv,"\t\tsSQL += \""+str+"\";");
		}
		outline(outCsv,"\t\tsSQL += \" FROM "+csvtbl.toUpperCase()+"\";");
		outline(outCsv,"\t\t");
		outline(outCsv,"\t\tif ("+utilsname+".checkMust(where)) {");
		outline(outCsv,"\t\t\tsSQL += \" WHERE \"+where;");
		outline(outCsv,"\t\t}");
		outline(outCsv,"\t\tif ("+utilsname+".checkMust(order)) {");
		outline(outCsv,"\t\t\tsSQL += \" ORDER BY \"+order;");
		outline(outCsv,"\t\t}");
		//▽2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
//		outline(outCsv,"\t\tif ("+utilsname+".checkMust(limit)) {");
//		outline(outCsv,"\t\t\tsSQL += \" LIMIT \"+limit;");
//		outline(outCsv,"\t\t}");
		//△2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
		outline(outCsv,"\t\t");
		outline(outCsv,"\t\tlog.info(sSQL);");
		outline(outCsv,"\t\tcond.setSQL(sSQL);");
		outline(outCsv,"\t\trs = dao.select(cond);");
		outline(outCsv,"\t}");
		outline(outCsv,"");

		//next句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")の次レコード読み込み");
		outline(outCsv,"\t * @param dao");
		outline(outCsv,"\t * @return 次レコード有り:true なし:false");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		outline(outCsv,"\tpublic boolean next() throws Exception {");
		outline(outCsv,"");
		outline(outCsv,"\t\tif (rs == null) {");
		outline(outCsv,"\t\t\tthrow new RuntimeException (\"RecordSet not open\");");
		outline(outCsv,"\t\t}");
		outline(outCsv,"");
		outline(outCsv,"\t\tinit();");
		outline(outCsv,"");
		outline(outCsv,"\t\tif (rs.next()) {");
		for(String str:ari) {
			outline(outCsv,"\t\t"+str);
		}
		outline(outCsv,"\t\t\treturn true;");
		outline(outCsv,"\t\t}");
		outline(outCsv,"\t\treturn false;");
		outline(outCsv,"\t}");
		outline(outCsv,"");

		//close句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")のレコードセットクローズ");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		outline(outCsv,"\tpublic void close() throws Exception {");
		outline(outCsv,"");
		outline(outCsv,"\t\tif (rs == null) {");
		outline(outCsv,"\t\t\tthrow new RuntimeException (\"RecordSet not open\");");
		outline(outCsv,"\t\t}");
		//▽2020.09.07 sakurai close内にinitがあるとreturnで返す際にrsを閉じれないか、結果がnullになるかの2択になってしまうので削除
//		outline(outCsv,"");
//		outline(outCsv,"\t\tinit();");
		//△2020.09.07 sakurai close内にinitがあるとreturnで返す際にrsを閉じれないか、結果がnullになるかの2択になってしまうので削除
		outline(outCsv,"");
		outline(outCsv,"\t\trs.close();");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		
		//read句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")の読み込み");
		outline(outCsv,"\t * @param dao");
		for(String str:param) {
			outline(outCsv,str);
		}
		outline(outCsv,"\t * @param addWhere 追加Where句");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		String read = "\tpublic boolean read_"+methodName;
		read += "(Dao dao"+uke+", String addWhere) throws Exception {";
		outline(outCsv,read);
		outline(outCsv,"");
		outline(outCsv,"\t\tboolean ret = false;");
		outline(outCsv,"");
		
//		for(String str:shokika) {
//			outline(outCsv,str);
//		}
		outline(outCsv,"\t\tif (addWhere == null) { addWhere = \"\"; }");
		outline(outCsv,"");
		outline(outCsv,"\t\tinit();");
		outline(outCsv,"");
		outline(outCsv,"\t\tString sSQL = \"SELECT \";");
		for(String str:koumoku) {
			outline(outCsv,"\t\tsSQL += \""+str+"\";");
		}
		outline(outCsv,"\t\tsSQL += \" FROM "+csvtbl.toUpperCase()+"\";");
		outline(outCsv,"\t\tsSQL += \" WHERE "+where+" \" + addWhere;");
		outline(outCsv,"\t\tlog.info(sSQL);");
		outline(outCsv,"\t\tcond.setSQL(sSQL);");
		outline(outCsv,"\t\trs = dao.select(cond);");
		outline(outCsv,"");
		outline(outCsv,"\t\tif (rs.next()){");
		
		for(String str:ari) {
			outline(outCsv,"\t\t"+str);
		}
		
		outline(outCsv,"\t\t\tret = true;");
		outline(outCsv,"\t\t}");
//		outline(outCsv,"\t\t} else {");
//		
//		for(String str:nasi) {
//			outline(outCsv,"\t\t"+str);
//		}
//		
//		outline(outCsv,"\t\t}");
		
		outline(outCsv,"\t\tcond.clear();");
		outline(outCsv,"\t\trs.close();");
		outline(outCsv,"\t\treturn ret;");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		
		//update句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")の更新");
		outline(outCsv,"\t * @param dao");
		for(String str:param) {
			outline(outCsv,str);
		}
		
		outline(outCsv,"\t * @param addWhere 追加Where句");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		String update = "\tpublic int update_"+methodName;
		update += "(Dao dao"+uke+", String addWhere) throws Exception {";
		outline(outCsv,update);
		outline(outCsv,"");
		
		outline(outCsv,"\t\tif (addWhere == null) { addWhere = \"\"; }");
		outline(outCsv,"");
		outline(outCsv,"\t\tString sSQL = \"\";");
		outline(outCsv,"");
		for(String str:koumoku2) {
				String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
				//▽2020.10.02 sakurai UPCNT,UPDTIME自動セット対応
				if(str.equals("UPCNT") || str.equals("UPDTIME")){
					;
				}else{					
					if(checkJavaYoyaku(str)){
						outline(outCsv,"\t\tif (get"+headUpperBnm+"Yoyaku()!=null) {");
					}else{
						outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
					}
				}
				//△2020.10.02 sakurai UPCNT,UPDTIME自動セット対応
				//△2020.09.02 sakurai 予約語対応
				
//				outline(outCsv,"\t\t\tif (sSQL != \"\") { sSQL+=\",\"; }");
				//▽2020.10.19 sakurai Stringエラー対応
				outline(outCsv,"\t\t\tif (!sSQL.equals(\"\")) { sSQL+=\",\"; }");
				//△2020.10.19 sakurai Stringエラー対応
				
				
	//			outline(outCsv,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"().replace(\"\\'\", \"\\\\'\")"+"+\"'\";");
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"()"+"+\"'\";");
				if(str.equals("UPCNT")){
					outline(outCsv,"\t\t\tsSQL += \" \\\""+str+"\\\"='UPCNT + 1'\";");
				}else if(str.equals("UPDTIME")){
					outline(outCsv,"\t\t\tsSQL += \" "+str+"='\"+DateUtils.getNowTime()"+"+\"'\";");
				}else{					
					if(checkOracleYoyaku(str)){
						if(checkJavaYoyaku(str)){
							outline(outCsv,"\t\t\tsSQL += \" \\\""+str+"\\\"='\"+get"+headUpperBnm+"Yoyaku()"+"+\"'\";");
						}else{						
							outline(outCsv,"\t\t\tsSQL += \" \\\""+str+"\\\"='\"+get"+headUpperBnm+"()"+"+\"'\";");
						}
					}else{
						if(checkJavaYoyaku(str)){
							outline(outCsv,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"Yoyaku()"+"+\"'\";");
						}else{						
							outline(outCsv,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"()"+"+\"'\";");
						}
					}
				}
				//△2020.09.02 sakurai 予約語対応
				outline(outCsv,"\t\t}");
		}
		outline(outCsv,"\t\tsSQL += \" WHERE "+where+" \"+addWhere;");
		outline(outCsv,"\t\tsSQL = \"UPDATE "+csvtbl+" SET \" + sSQL;");
		outline(outCsv,"\t\tlog.info(sSQL);");
		outline(outCsv,"\t\tcond.setSQL(sSQL);");
		outline(outCsv,"\t\treturn dao.execute(cond);");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		
		//insert句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")の作成");
		outline(outCsv,"\t * @param dao");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		String insert = "\tpublic int insert_"+methodName;
		insert += "(Dao dao) throws Exception {";
		outline(outCsv,insert);
		outline(outCsv,"");
		outline(outCsv,"\t\tString sColumns=\"\";");
		outline(outCsv,"");
		int count = 0;
		for(String str:koumoku2) {
			if(count > 0){
				String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
				if(checkJavaYoyaku(str)){
					outline(outCsv,"\t\tif (get"+headUpperBnm+"Yoyaku()!=null) {");
				}else{
					outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
				}
				//△2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\t\tif (sColumns != \"\") { sColumns+=\",\"; }");
				//▽2020.10.19 sakurai Stringエラー対応
				outline(outCsv,"\t\t\tif (!sColumns.equals(\"\")) { sColumns+=\",\"; }");
				//△2020.10.19 sakurai Stringエラー対応
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\t\tsColumns += \" "+str+"\"; ");
				if(checkOracleYoyaku(str)){
					outline(outCsv,"\t\t\tsColumns += \" \\\""+str+"\\\"\"; ");
					
				}else{
					outline(outCsv,"\t\t\tsColumns += \" "+str+"\"; ");
				}
				//△2020.09.02 sakurai 予約語対応
				outline(outCsv,"\t\t}");
			}
			count++;
		}
		outline(outCsv,"");
		outline(outCsv,"\t\tString sValues=\"\";");
		outline(outCsv,"");
		//▽2020.7.14 sakurai IDを非表示にする。
//		for(String str:koumoku2) {
//			String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
//			outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
//			outline(outCsv,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
//			outline(outCsv,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"()"+"+\"'\";");
////			outline(outCsv,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"().replace(\"\\'\", \"\\\\'\")"+"+\"'\";");
//			outline(outCsv,"\t\t}");
//		}
		count = 0;
		for(String str:koumoku2) {
			if(count > 0){
				String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
//				outline(outCsv,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
//				outline(outCsv,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"()"+"+\"'\";");
				if(checkJavaYoyaku(str)){					
					outline(outCsv,"\t\tif (get"+headUpperBnm+"Yoyaku()!=null) {");
//					outline(outCsv,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
					//▽2020.10.19 sakurai Stringエラー対応
					outline(outCsv,"\t\t\tif (!sValues.equals(\"\")) { sValues+=\",\"; }");
					//△2020.10.19 sakurai Stringエラー対応
					outline(outCsv,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"Yoyaku()"+"+\"'\";");
				}else{
					outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
					outline(outCsv,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
					//▽2020.10.19 sakurai Stringエラー対応
//					outline(outCsv,"\t\t\tif (!sValues.equals(\"\")) { sValues+=\",\"; }");
					//△2020.10.19 sakurai Stringエラー対応
					outline(outCsv,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"()"+"+\"'\";");
				}
				//△2020.09.02 sakurai 予約語対応
	//			outline(outCsv,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"().replace(\"\\'\", \"\\\\'\")"+"+\"'\";");
				outline(outCsv,"\t\t}");
			}
			count++;
		}
		//△2020.7.14 sakurai IDを非表示にする。
		outline(outCsv,"");
		outline(outCsv,"\t\tString sSQL=\"\";");
		//▽2020.7.14 sakurai IDを追加
//		outline(outCsv,"\t\tsSQL  = \"INSERT INTO "+csvtbl+" ( \"+sColumns;");
//		outline(outCsv,"\t\tsSQL += \" ) VALUES (\"+sValues;");
		outline(outCsv,"\t\tsSQL  = \"INSERT INTO "+csvtbl+" (" + csvtbl + "ID, \"+sColumns;");
		outline(outCsv,"\t\tsSQL += \" ) VALUES (" + csvtbl + "_ID_SEQ.NEXTVAL, \"+sValues;");
		//△2020.7.14 sakurai IDを追加
		outline(outCsv,"\t\tsSQL += \" ) \";");
		outline(outCsv,"\t\tlog.info(sSQL);");
		outline(outCsv,"\t\tcond.setSQL(sSQL);");
		outline(outCsv,"\t\treturn dao.execute(cond);");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		
		//delete句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")の削除");
		outline(outCsv,"\t * @param dao");
		for(String str:param) {
			outline(outCsv,str);
		}
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		String delete = "\tpublic int delete_"+methodName;
		delete += "(Dao dao"+uke+") throws Exception {";
		outline(outCsv,delete);
		
		outline(outCsv,"");
		outline(outCsv,"\t\tString sSQL = \"DELETE \";");
		outline(outCsv,"\t\tsSQL += \" FROM "+csvtbl.toUpperCase()+"\";");
		outline(outCsv,"\t\tsSQL += \" WHERE "+where+"\";");
		outline(outCsv,"\t\tlog.info(sSQL);");
		outline(outCsv,"\t\tcond.setSQL(sSQL);");
		outline(outCsv,"\t\treturn dao.execute(cond);");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		
		//set句の作成
		//▽2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * "+tbljnm+"("+tblnm+")の設定");
		outline(outCsv,"\t * @param kmskk 値を保持しているtool");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		String set = "\tpublic " + methodName + "_tool set_"+methodName;
		set += "(" +methodName+"_tool inTool) throws Exception {";
		outline(outCsv,set);
		outline(outCsv,"");
		outline(outCsv,"\t\t" + methodName + "_tool tmpTool = new "+ methodName + "_tool();");
		outline(outCsv,"");
		for(String str:setlist) {
			outline(outCsv,str);
		}
		outline(outCsv,"");
		outline(outCsv,"\t\treturn tmpTool;");
		outline(outCsv,"\t}");
		outline(outCsv,"");
		//△2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		
		
		//初期化句の作成
		outline(outCsv,"\t/**");
		outline(outCsv,"\t * 初期化");
		outline(outCsv,"\t * @throws Exception");
		outline(outCsv,"\t */");
		outline(outCsv,"\tpublic void init() throws Exception {");
		for(String str:nasi) {
			outline(outCsv,"\t"+str);
		}
		outline(outCsv,"\t}");
		
		
		for(String str:tter) {
			outline(outCsv,str);
		}
		
		outline(outCsv,"}");
		
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
	}
	
	/**
	 * JavaCode作成
	 * 
	 * @param outCsv csvライター
	 * @throws Exception
	 */
	private void makeJavaCode2(String csvtbl) throws Exception {
		
//		log.info("-------------------------------------------------------------------");
//		log.info("【"+csvtbl+"】　START");
		
		//データベースを読み書きする準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		String tblnm="";
		String tbljnm="";
		String where="";
		String uke="";
		
		String utilsname = "LafitUtils";
		
		if (getLoginBean().getKc().equals("81")) {
			utilsname = "AtgUtils";
		}
		
		boolean timestamp = false;
		
		//検索SQLの作成
		String sql = "";
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLJNM";
		sql += " FROM ZM_TBL";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		if (rs.next()) {
			tbljnm = rs.getStringValue("TBLJNM");
		}
		rs.close();
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		sql += " ORDER BY TBLNM,TBLNO";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		int linecnt = 0;
		
		ArrayList<String> teigi = new ArrayList<String>();
		ArrayList<String> shokika = new ArrayList<String>();
		ArrayList<String> koumoku = new ArrayList<String>();
		ArrayList<String> koumoku2 = new ArrayList<String>();
		ArrayList<String> ari = new ArrayList<String>();
		ArrayList<String> nasi = new ArrayList<String>();
		ArrayList<String> tter = new ArrayList<String>();
		ArrayList<String> param = new ArrayList<String>();
		//▽2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		ArrayList<String> setlist = new ArrayList<String>();
		//△2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		
		while (rs.next()) {
			
			String rnm = rs.getStringValue("RNM");
			String bnm = rs.getStringValue("BNM");
			String kata = rs.getStringValue("KATA");
			String lng1 = rs.getStringValue("LNG1");
			String lng2 = rs.getStringValue("LNG2");
			String tkey1 = rs.getStringValue("TKEY01");
			tblnm = rs.getStringValue("TBLNM");
			
			//頭1文字が大文字のBnm
			String headUpperBnm = bnm.toUpperCase().substring(0,1)+bnm.toLowerCase().substring(1);
			
			//定義句の作成
			teigi.add("\t/**");
			teigi.add("\t * "+rnm);
			if (kata.equals("9")) {
				teigi.add("\t * "+bnm+" "+kata+"("+lng1+","+lng2+")");
			} else if (kata.equals("T")) {
				teigi.add("\t * "+bnm+" "+kata);
			} else {
				teigi.add("\t * "+bnm+" "+kata+"("+lng1+")");
			}
			teigi.add("\t */");
			
			//▽2020.09.02 sakurai 予約語対応
//			if (kata.equals("9")) {
//				if (lng2.equals("0")) {
//					teigi.add("\tprivate Long "+bnm.toLowerCase()+";");
//				} else {
//					teigi.add("\tprivate Double "+bnm.toLowerCase()+";");
//				}
//			} else if (kata.equals("X") || kata.equals("V")) {
//				teigi.add("\tprivate String "+bnm.toLowerCase()+";");
//			} else if (kata.equals("T")) {
//				teigi.add("\tprivate Timestamp "+bnm.toLowerCase()+";");
//			}
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					if(checkJavaYoyaku(bnm)){
						teigi.add("\tprivate Long "+bnm.toLowerCase()+"Yoyaku;");
					}else{						
						teigi.add("\tprivate Long "+bnm.toLowerCase()+";");
					}
				} else {
					if(checkJavaYoyaku(bnm)){
						teigi.add("\tprivate Double "+bnm.toLowerCase()+"Yoyaku;");
					}else{						
						teigi.add("\tprivate Double "+bnm.toLowerCase()+";");
					}
				}
			} else if (kata.equals("X") || kata.equals("V")) {
				if(checkJavaYoyaku(bnm)){
					teigi.add("\tprivate String "+bnm.toLowerCase()+"Yoyaku;");
				}else{					
					teigi.add("\tprivate String "+bnm.toLowerCase()+";");
				}
			} else if (kata.equals("T")) {
				if(checkJavaYoyaku(bnm)){
					teigi.add("\tprivate Timestamp "+bnm.toLowerCase()+"Yoyaku;");
				}else{					
					teigi.add("\tprivate Timestamp "+bnm.toLowerCase()+";");
				}
			}
			//△2020.09.02 sakurai 予約語対応
			teigi.add("");
			
			//初期化句の作成
			//▽2020.09.02 sakurai 予約語対応
//			if (kata.equals("9")) {
//				if (lng2.equals("0")) {
//					shokika.add("\t\t"+bnm.toLowerCase()+"=0L;\t// "+rnm);
//				} else {
//					shokika.add("\t\t"+bnm.toLowerCase()+"=0D;\t// "+rnm);
//				}
//			} else if (kata.equals("T")) {
//				shokika.add("\t\t"+bnm.toLowerCase()+"=null;\t// "+rnm);
//			} else {
//				shokika.add("\t\t"+bnm.toLowerCase()+"=\"\";\t// "+rnm);
//			}
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					if(checkJavaYoyaku(bnm)){
						shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=0L;\t// "+rnm);
					}else{						
						shokika.add("\t\t"+bnm.toLowerCase()+"=0L;\t// "+rnm);
					}
				} else {
					if(checkJavaYoyaku(bnm)){
						shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=0D;\t// "+rnm);
					}else{						
						shokika.add("\t\t"+bnm.toLowerCase()+"=0D;\t// "+rnm);
					}
				}
			} else if (kata.equals("T")) {
				if(checkJavaYoyaku(bnm)){
					shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=null;\t// "+rnm);
				}else{					
					shokika.add("\t\t"+bnm.toLowerCase()+"=null;\t// "+rnm);
				}
			} else {
				if(checkJavaYoyaku(bnm)){
					shokika.add("\t\t"+bnm.toLowerCase()+"Yoyaku=\"\";\t// "+rnm);
				}else{					
					shokika.add("\t\t"+bnm.toLowerCase()+"=\"\";\t// "+rnm);
				}
			}
			//△2020.09.02 sakurai 予約語対応
			
			//項目句の作成
			//▽2020.09.02 sakurai 予約語対応
//			if (linecnt == 0) {
//				koumoku.add("\t "+bnm.toUpperCase());
//			} else {
//				koumoku.add("\t,"+bnm.toUpperCase());
//			}
			if (linecnt == 0) {
				if(checkOracleYoyaku(bnm)){
					koumoku.add("\t \\\""+bnm.toUpperCase()+"\\\"");
				}else{					
					koumoku.add("\t "+bnm.toUpperCase());
				}
			} else {
				if(checkOracleYoyaku(bnm)){
					koumoku.add("\t,\\\""+bnm.toUpperCase()+"\\\"");
				}else{
					koumoku.add("\t,"+bnm.toUpperCase());					
				}
			}
			//△2020.09.02 sakurai 予約語対応
			koumoku2.add(bnm.toUpperCase());
			
			//存在した場合の代入句
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					//▽2020.08.28 sakurai im移行
//					ari.add("\t"+bnm.toLowerCase()+"=CommonUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
					//▽2020.09.02 sakurai 予約語対応
//					ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
//					if(checkOracleYoyaku(bnm)){
//						if(checkJavaYoyaku(bnm)){
//							ari.add("\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getLongValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");							
//						}else{
//							ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");
//						}
//					}else{
//						if(checkJavaYoyaku(bnm)){
//							ari.add("\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");							
//						}else{
//							ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
//						}
//					}
					if(checkOracleYoyaku(bnm)){
						ari.add("\tif(rs.get(\"\\\""+bnm+"\\\"\") != null){");
						if(checkJavaYoyaku(bnm)){
							ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getLongValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");							
						}else{
							ari.add("\t\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");
						}
					}else{
						ari.add("\tif(rs.get(\""+bnm+"\") != null){");
						if(checkJavaYoyaku(bnm)){
							ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");							
						}else{
							ari.add("\t\t"+bnm.toLowerCase()+"=LafitUtils.getLongValue(rs.getStringValue(\""+bnm+"\"));");
						}
					}
					ari.add("\t}else{");
					if(checkJavaYoyaku(bnm)){						
						ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku = null;");
					}else{
						ari.add("\t\t"+bnm.toLowerCase()+" = null;");
					}
					ari.add("\t}");
					//△2020.09.02 sakurai 予約語対応
					//△2020.08.28 sakurai im移行
				} else {
					//▽2020.08.28 sakurai im移行
//					ari.add("\t"+bnm.toLowerCase()+"=CommonUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");
					//▽2020.09.02 sakurai 予約語対応
//					ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");
					if(checkOracleYoyaku(bnm)){
						if(checkJavaYoyaku(bnm)){
							ari.add("\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getDoubleValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");							
						}else{
							ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getDoubleValue(rs.getStringValue(\"\\\""+bnm+"\\\"\"));");
						}
					}else{
						if(checkJavaYoyaku(bnm)){
							ari.add("\t"+bnm.toLowerCase()+"Yoyaku=LafitUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");							
						}else{
							ari.add("\t"+bnm.toLowerCase()+"=LafitUtils.getDoubleValue(rs.getStringValue(\""+bnm+"\"));");
						}
					}
					//△2020.09.02 sakurai 予約語対応
					//△2020.08.28 sakurai im移行
				}
			} else if (kata.equals("T")) {
				//▽2020.09.02 sakurai 予約語対応
//				ari.add("\t"+bnm.toLowerCase()+"=Timestamp.valueOf(rs.get(\""+bnm+"\").toString());");
				ari.add("\tif(rs.get(\""+bnm+"\") != null){");
				if(checkOracleYoyaku(bnm)){
					if(checkJavaYoyaku(bnm)){						
						ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku=Timestamp.valueOf(rs.get(\"\\\""+bnm+"\\\"\").toString());");
					}else{
						ari.add("\t\t"+bnm.toLowerCase()+"=Timestamp.valueOf(rs.get(\"\\\""+bnm+"\\\"\").toString());");
					}
				}else{
					if(checkJavaYoyaku(bnm)){
						ari.add("\t\t"+bnm.toLowerCase()+"Yoyaku=Timestamp.valueOf(rs.get(\""+bnm+"\").toString());");
					}else{
						ari.add("\t\t"+bnm.toLowerCase()+"=Timestamp.valueOf(rs.get(\""+bnm+"\").toString());");
					}
				}
				ari.add("\t}else{");
				ari.add("\t\t"+bnm.toLowerCase()+"=null;");
				ari.add("\t}");
				//△2020.09.02 sakurai 予約語対応
				timestamp = true;
			} else {
				//▽2020.09.02 sakurai 予約語対応
//				ari.add("\t"+bnm.toLowerCase()+"=rs.getStringValue(\""+bnm+"\");");
				if(checkOracleYoyaku(bnm)){
					if(checkJavaYoyaku(bnm)){						
						ari.add("\t"+bnm.toLowerCase()+"Yoyaku=rs.getStringValue(\"\\\""+bnm+"\\\"\");");
					}else{
						ari.add("\t"+bnm.toLowerCase()+"=rs.getStringValue(\"\\\""+bnm+"\\\"\");");
					}
				}else{
					if(checkJavaYoyaku(bnm)){
						ari.add("\t"+bnm.toLowerCase()+"Yoyaku=rs.getStringValue(\""+bnm+"\");");
					}else{						
						ari.add("\t"+bnm.toLowerCase()+"=rs.getStringValue(\""+bnm+"\");");
					}
				}
				//△2020.09.02 sakurai 予約語対応
			}
			
			//存在しない場合の代入句
			//▽2020.09.02 sakurai 予約語対応
//			nasi.add("\t"+bnm.toLowerCase()+"=null;");
			if(checkJavaYoyaku(bnm)){
				nasi.add("\t"+bnm.toLowerCase()+"Yoyaku=null;");
			}else{
				nasi.add("\t"+bnm.toLowerCase()+"=null;");
			}
			//△2020.09.02 sakurai 予約語対応
			//Getter,Setter
			tter.add("\t/**");
			tter.add("\t * "+rnm+"を取得します。<br>");
			if (kata.equals("9")) {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+","+lng2+")");
			} else if (kata.equals("T")) {
				tter.add("\t * "+bnm+" "+kata);
			} else {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+")");
			}
			tter.add("\t * @return "+rnm);
			tter.add("\t */");
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic Long get"+headUpperBnm+"() {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic Long get"+headUpperBnm+"Yoyaku() {");
					}else{						
						tter.add("\tpublic Long get"+headUpperBnm+"() {");
					}
					//△2020.09.02 sakurai 予約語対応
				} else {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic Double get"+headUpperBnm+"() {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic Double get"+headUpperBnm+"Yoyaku() {");
					}else{						
						tter.add("\tpublic Double get"+headUpperBnm+"() {");
					}
					//△2020.09.02 sakurai 予約語対応
				}
			} else if (kata.equals("T")) {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic Timestamp get"+headUpperBnm+"() {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic Timestamp get"+headUpperBnm+"Yoyaku() {");
				}else{						
					tter.add("\tpublic Timestamp get"+headUpperBnm+"() {");
				}
				//△2020.09.02 sakurai 予約語対応
			} else {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic String get"+headUpperBnm+"() {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic String get"+headUpperBnm+"Yoyaku() {");
				}else{						
					tter.add("\tpublic String get"+headUpperBnm+"() {");
				}
				//△2020.09.02 sakurai 予約語対応
			}
			//▽2020.09.02 sakurai 予約語対応
//			tter.add("\t\treturn "+bnm.toLowerCase()+";");
			if(checkJavaYoyaku(bnm)){
				tter.add("\t\treturn "+bnm.toLowerCase()+"Yoyaku;");
			}else{
				tter.add("\t\treturn "+bnm.toLowerCase()+";");
			}
			//△2020.09.02 sakurai 予約語対応
			tter.add("\t}");
			tter.add("");
			
			tter.add("\t/**");
			tter.add("\t * "+rnm+"を設定します。<br>");
			if (kata.equals("9")) {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+","+lng2+")");
			} else if (kata.equals("T")) {
				tter.add("\t * "+bnm+" "+kata);
			} else {
				tter.add("\t * "+bnm+" "+kata+"("+lng1+")");
			}
			tter.add("\t * @param "+rnm);
			tter.add("\t */");
			if (kata.equals("9")) {
				if (lng2.equals("0")) {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic void set"+headUpperBnm+"(Long "+bnm.toLowerCase()+") {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(Long "+bnm.toLowerCase()+"Yoyaku) {");
					}else{
						tter.add("\tpublic void set"+headUpperBnm+"(Long "+bnm.toLowerCase()+") {");
					}
					//△2020.09.02 sakurai 予約語対応
				} else {
					//▽2020.09.02 sakurai 予約語対応
//					tter.add("\tpublic void set"+headUpperBnm+"(Double "+bnm.toLowerCase()+") {");
					if(checkJavaYoyaku(bnm)){
						tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(Double "+bnm.toLowerCase()+"Yoyaku) {");
					}else{
						tter.add("\tpublic void set"+headUpperBnm+"(Double "+bnm.toLowerCase()+") {");
					}
					//△2020.09.02 sakurai 予約語対応
				}
			} else if (kata.equals("T")) {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic void set"+headUpperBnm+"(Timestamp "+bnm.toLowerCase()+") {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(Timestamp "+bnm.toLowerCase()+"Yoyaku) {");
				}else{
					tter.add("\tpublic void set"+headUpperBnm+"(Timestamp "+bnm.toLowerCase()+") {");
				}
				//△2020.09.02 sakurai 予約語対応
			} else {
				//▽2020.09.02 sakurai 予約語対応
//				tter.add("\tpublic void set"+headUpperBnm+"(String "+bnm.toLowerCase()+") {");
				if(checkJavaYoyaku(bnm)){
					tter.add("\tpublic void set"+headUpperBnm+"Yoyaku(String "+bnm.toLowerCase()+"Yoyaku) {");
				}else{
					tter.add("\tpublic void set"+headUpperBnm+"(String "+bnm.toLowerCase()+") {");
				}
				//△2020.09.02 sakurai 予約語対応
			}
			//▽2020.09.02 sakurai 予約語対応
//			tter.add("\t\tthis."+bnm.toLowerCase()+"="+bnm.toLowerCase()+";");
			if(checkJavaYoyaku(bnm)){
				tter.add("\t\tthis."+bnm.toLowerCase()+"Yoyaku="+bnm.toLowerCase()+"Yoyaku;");
			}else{
				tter.add("\t\tthis."+bnm.toLowerCase()+"="+bnm.toLowerCase()+";");
			}
			//△2020.09.02 sakurai 予約語対応
			tter.add("\t}");
			tter.add("");
			
			//主キーから取得
			if (tkey1.equals("U")) {
				
				// WHERE句を作成
				if (!where.equals("")) {
					where +=" AND ";
				}
				if (kata.equals("9")) {
					where += bnm+"=\"+in"+headUpperBnm+"+\"";
				} else {
					where += bnm+"='\"+in"+headUpperBnm+"+\"'";
				}
				
				// readXXX()内を作成
				if (kata.equals("9")) {
					if (lng2.equals("0")) {
						uke += ",Long in"+headUpperBnm;
					} else {
						uke += ",Double in"+headUpperBnm;
					}
				} else if (kata.equals("T")) {
					uke += ",Timestamp in"+headUpperBnm;
				} else {
					uke += ",String in"+headUpperBnm;
				}
				
				// メソッドコメントを作成
				param.add("\t * @param in"+headUpperBnm+" "+rnm);
				
			}
			
			//▽2020.09.18 sakurai setメソッド追加 
			if(checkJavaYoyaku(bnm)){
				setlist.add("\t\ttmpTool.set"+headUpperBnm+"Yoyaku(inTool.get"+headUpperBnm+"Yoyaku());");
			}else{
				setlist.add("\t\ttmpTool.set"+headUpperBnm+"(inTool.get"+headUpperBnm+"());");
			}
			//△2020.09.18 sakurai setメソッド追加 
			
			linecnt++;
		}
		
		//順番にJavaコードを組み立てる
		
		//メソッド名称の設定
		String methodName = csvtbl.toUpperCase();
//		String methodName = csvtbl.toUpperCase().substring(0,1)+csvtbl.toUpperCase().substring(1,2)+"_"+csvtbl.toUpperCase().substring(3,4)+csvtbl.toUpperCase().substring(4);
		
		CsvPrinter outCsv2;
		outCsv2 = new CsvPrinter();
//		outCsv2.setFilePath("D:\\KC05\\tool\\");
		outCsv2.setFilePath("D:\\iap\\eBuilder8\\workspace\\Lafit\\src\\main\\java\\jp\\co\\tisa\\lafit\\bl\\entity_tool\\");
		outCsv2.setFileName(methodName+"_tool.java");
		outCsv2.CsvInit();
		
		if (utilsname.equals("AtgUtils")) {
			outline2(outCsv2,"package jp.co.tisa.atg.tools;");
		} else {
			//▽2020.7.14 sakurai im移行
//			outline2(outCsv2,"package jp.co.tisa.lafit.tools;");
			outline2(outCsv2,"package jp.co.tisa.lafit.bl.entity_tool;");
			//△2020.7.14 sakurai im移行
		}
		outline2(outCsv2,"");
		
		if (timestamp == true) {
			outline2(outCsv2,"import java.sql.Timestamp;");
			outline2(outCsv2,"");
		}
		
		if (utilsname.equals("AtgUtils")) {
			outline2(outCsv2,"import jp.co.tisa.atg.base.BusinessBeanBase;");
			outline2(outCsv2,"import jp.co.tisa.atg.base.common.util.function.AtgUtils;");
			outline2(outCsv2,"import jp.co.tisa.atg.base.common.util.function.CommonUtils;");
			outline2(outCsv2,"import jp.co.tisa.atg.base.dao.Condition;");
			outline2(outCsv2,"import jp.co.tisa.atg.base.dao.Dao;");
			outline2(outCsv2,"import jp.co.tisa.atg.base.dao.RecordSet;");
		} else {
			//▽2020.7.14 sakurai im移行
//			outline2(outCsv2,"import jp.co.tisa.lafit.base.BusinessBeanBase;");
//			outline2(outCsv2,"import jp.co.tisa.lafit.base.dao.Dao;");
//			outline2(outCsv2,"import jp.co.tisa.lafit.base.dao.Condition;");
//			outline2(outCsv2,"import jp.co.tisa.lafit.base.dao.RecordSet;");
//			outline2(outCsv2,"import jp.co.tisa.lafit.base.common.util.function.CommonUtils;");
			outline2(outCsv2,"import jp.co.tisa.lafit.framework.base.BlBase;");
			outline2(outCsv2,"import jp.co.tisa.lafit.framework.dao.Dao;");
			outline2(outCsv2,"import jp.co.tisa.lafit.framework.dao.Condition;");
			outline2(outCsv2,"import jp.co.tisa.lafit.framework.dao.RecordSet;");
			//▽2020.09.03 sakurai util削除対応
//			outline2(outCsv2,"import jp.co.tisa.lafit.framework.util.LafitUtils;");
			outline2(outCsv2,"import jp.co.tisa.lafit.framework.utils.LafitUtils;");
			outline2(outCsv2,"import jp.co.tisa.lafit.framework.utils.DateUtils;");
			//△2020.09.03 sakurai util削除対応
			//△2020.7.14 sakurai im移行
			outline2(outCsv2,"import javax.persistence.NoResultException;");
		}
		
		outline2(outCsv2,"");
		outline2(outCsv2,"/**");
		outline2(outCsv2," * "+tbljnm+"("+tblnm+")取得");
		outline2(outCsv2," * <p>");
		outline2(outCsv2," * @author 自動生成");
		outline2(outCsv2," */");
		//▽2020.7.14 sakurai im移行
//		outline2(outCsv2,"public class "+methodName+"_tool extends BusinessBeanBase {");
//		outline2(outCsv2,"@SuppressWarnings(\"serial\")");
		outline2(outCsv2,"public class "+methodName+"_tool extends BlBase {");
		//△2020.7.14 sakurai im移行
		outline2(outCsv2,"");
		
		//定義の作成
		
		for(String str:teigi) {
			outline2(outCsv2,str);
		}
		
		outline2(outCsv2,"\tRecordSet rs = null;");
		outline2(outCsv2,"\tCondition cond = new Condition();");
		outline2(outCsv2,"");
		
		outline2(outCsv2,"\t//レコードカウント用変数");
		outline2(outCsv2,"\tprivate int iCnt = 0;");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t//1件取得フラグ");
		outline2(outCsv2,"\tprivate boolean singleResultFlg = false;");
		outline2(outCsv2,"");

		//コンストラクタの作成
		
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * コンストラクタ");
		outline2(outCsv2,"\t * <p>");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		outline2(outCsv2,"\tpublic "+methodName+"_tool() throws Exception {");
		outline2(outCsv2,"\t\tsuper();");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
		
		
		
		
		
		//list句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")のレコードセットの作成");
		outline2(outCsv2,"\t * @param dao");
		outline2(outCsv2,"\t * @param where where句");
		outline2(outCsv2,"\t * @param order order句");
		outline2(outCsv2,"\t * @param inSingleResultFlg 1件取得フラグ");
		//▽2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
//		outline2(outCsv2,"\t * @param limit limit句");
		//△2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		String list = "\tpublic void list_"+methodName;
		//▽2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
//		list += "(Dao dao, String where, String order, String limit) throws Exception {";
		list += "(Dao dao, String where, String order, boolean inSingleResultFlg) throws Exception {";
		//△2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
		outline2(outCsv2,list);
		outline2(outCsv2,"");
		
		outline2(outCsv2,"\t\tinit();");
		outline2(outCsv2,"\t\t// 1件取得フラグを設定する。");
		outline2(outCsv2,"\t\tthis.singleResultFlg = inSingleResultFlg;");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sSQL = \"SELECT \";");
		for(String str:koumoku) {
			outline2(outCsv2,"\t\tsSQL += \""+str+"\";");
		}
		outline2(outCsv2,"\t\tsSQL += \" FROM "+csvtbl.toUpperCase()+"\";");
		outline2(outCsv2,"\t\t");
		outline2(outCsv2,"\t\tif ("+utilsname+".checkMust(where)) {");
		outline2(outCsv2,"\t\t\tsSQL += \" WHERE \"+where;");
		outline2(outCsv2,"\t\t}");
		outline2(outCsv2,"\t\tif ("+utilsname+".checkMust(order)) {");
		outline2(outCsv2,"\t\t\tsSQL += \" ORDER BY \"+order;");
		outline2(outCsv2,"\t\t}");
		//▽2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
//		outline2(outCsv2,"\t\tif ("+utilsname+".checkMust(limit)) {");
//		outline2(outCsv2,"\t\t\tsSQL += \" LIMIT \"+limit;");
//		outline2(outCsv2,"\t\t}");
		//△2020.09.04 sakurai Oracleにおいてlimit句は使えない　対応
		outline2(outCsv2,"\t\t");
		outline2(outCsv2,"\t\tlog.info(sSQL);");
		outline2(outCsv2,"\t\tcond.setSQL(sSQL);");
		outline2(outCsv2,"\t\trs = dao.select(cond);");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");

		//next句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")の次レコード読み込み");
		outline2(outCsv2,"\t * @param dao");
		outline2(outCsv2,"\t * @return 次レコード有り:true なし:false");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		outline2(outCsv2,"\tpublic boolean next() throws Exception {");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tif (rs == null) {");
		outline2(outCsv2,"\t\t\tthrow new RuntimeException (\"RecordSet not open\");");
		outline2(outCsv2,"\t\t}");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tinit();");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tif (rs.next()) {");
		for(String str:ari) {
			outline2(outCsv2,"\t\t"+str);
		}
		outline2(outCsv2,"\t\t\tiCnt = iCnt + 1;");
		outline2(outCsv2,"\t\t\treturn true;");
		outline2(outCsv2,"\t\t}");
		outline2(outCsv2,"\t\t//return false;");
		outline2(outCsv2,"\t\t//データが1件でも存在していたかを判定");
		outline2(outCsv2,"\t\tif(singleResultFlg && iCnt == 0){");
		outline2(outCsv2,"\t\t\t//該当データ無しの場合はNoResultExceptionをスローする");
		outline2(outCsv2,"\t\t\tthrow new NoResultException();");
		outline2(outCsv2,"\t\t}else{");
		outline2(outCsv2,"\t\t\t//最終レコードとしてfalseを返却");
		outline2(outCsv2,"\t\t\treturn false;");
		outline2(outCsv2,"\t\t}");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");

		//close句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")のレコードセットクローズ");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		outline2(outCsv2,"\tpublic void close() throws Exception {");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tif (rs == null) {");
		outline2(outCsv2,"\t\t\tthrow new RuntimeException (\"RecordSet not open\");");
		outline2(outCsv2,"\t\t}");
		//▽2020.09.07 sakurai close内にinitがあるとreturnで返す際にrsを閉じれないか、結果がnullになるかの2択になってしまうので削除
//		outline2(outCsv2,"");
//		outline2(outCsv2,"\t\tinit();");
		//△2020.09.07 sakurai close内にinitがあるとreturnで返す際にrsを閉じれないか、結果がnullになるかの2択になってしまうので削除
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\t//データカウント変数を初期化");
		outline2(outCsv2,"\t\tiCnt = 0;");
		outline2(outCsv2,"\t\trs.close();");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
		
		//read句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")の読み込み");
		outline2(outCsv2,"\t * @param dao");
		for(String str:param) {
			outline2(outCsv2,str);
		}
		outline2(outCsv2,"\t * @param addWhere 追加Where句");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		String read = "\tpublic boolean read_"+methodName;
		read += "(Dao dao"+uke+", String addWhere) throws Exception {";
		outline2(outCsv2,read);
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tboolean ret = false;");
		outline2(outCsv2,"");
		
//		for(String str:shokika) {
//			outline2(outCsv2,str);
//		}
		outline2(outCsv2,"\t\tif (addWhere == null) { addWhere = \"\"; }");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tinit();");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sSQL = \"SELECT \";");
		for(String str:koumoku) {
			outline2(outCsv2,"\t\tsSQL += \""+str+"\";");
		}
		outline2(outCsv2,"\t\tsSQL += \" FROM "+csvtbl.toUpperCase()+"\";");
		outline2(outCsv2,"\t\tsSQL += \" WHERE "+where+" \" + addWhere;");
		outline2(outCsv2,"\t\tlog.info(sSQL);");
		outline2(outCsv2,"\t\tcond.setSQL(sSQL);");
		outline2(outCsv2,"\t\trs = dao.select(cond);");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tif (rs.next()){");
		
		for(String str:ari) {
			outline2(outCsv2,"\t\t"+str);
		}
		
		outline2(outCsv2,"\t\t\tret = true;");
		outline2(outCsv2,"\t\t}");
//		outline2(outCsv2,"\t\t} else {");
//		
//		for(String str:nasi) {
//			outline2(outCsv2,"\t\t"+str);
//		}
//		
//		outline2(outCsv2,"\t\t}");
		
		outline2(outCsv2,"\t\tcond.clear();");
		outline2(outCsv2,"\t\trs.close();");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tif(!ret){");
		outline2(outCsv2,"\t\t\t//該当データ無しの場合はNoResultExceptionをスローする");
		outline2(outCsv2,"\t\t\tthrow new NoResultException();");
		outline2(outCsv2,"\t\t}");
		outline2(outCsv2,"\t\treturn ret;");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
		
		//update句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")の更新");
		outline2(outCsv2,"\t * @param dao");
		for(String str:param) {
			outline2(outCsv2,str);
		}
		
		outline2(outCsv2,"\t * @param addWhere 追加Where句");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		String update = "\tpublic int update_"+methodName;
		update += "(Dao dao"+uke+", String addWhere) throws Exception {";
		outline2(outCsv2,update);
		outline2(outCsv2,"");
		
		outline2(outCsv2,"\t\tif (addWhere == null) { addWhere = \"\"; }");
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sSQL = \"\";");
		outline2(outCsv2,"");
		for(String str:koumoku2) {
				String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\tif (get"+headUpperBnm+"()!=null) {");
				//▽2020.10.02 sakurai UPCNT,UPDTIME自動セット対応
				if(str.equals("UPCNT") || str.equals("UPDTIME")){
					;
				}else{					
					if(checkJavaYoyaku(str)){
						outline2(outCsv2,"\t\tif (get"+headUpperBnm+"Yoyaku()!=null) {");
					}else{
						outline2(outCsv2,"\t\tif (get"+headUpperBnm+"()!=null) {");
					}
				}
				//△2020.10.02 sakurai UPCNT,UPDTIME自動セット対応
				//△2020.09.02 sakurai 予約語対応
				//▽2020.10.19 sakurai Stringエラー対応
//				outline2(outCsv2,"\t\t\tif (sSQL != \"\") { sSQL+=\",\"; }");
				outline2(outCsv2,"\t\t\tif (!sSQL.equals(\"\")) { sSQL+=\",\"; }");
				//△2020.10.19 sakurai Stringエラー対応
	//			outline2(outCsv2,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"().replace(\"\\'\", \"\\\\'\")"+"+\"'\";");
				//▽2020.09.02 sakurai 予約語対応
//				outline(outCsv,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"()"+"+\"'\";");
				if(str.equals("UPCNT")){
					outline2(outCsv2,"\t\tsSQL += \" "+str+" = UPCNT + 1\";");
				}else if(str.equals("UPDTIME")){
					outline2(outCsv2,"\t\tsSQL += \" "+str+"='\"+DateUtils.getNowTime()"+"+\"'\";");
				}else{					
					if(checkOracleYoyaku(str)){
						if(checkJavaYoyaku(str)){
							outline2(outCsv2,"\t\t\tsSQL += \" \\\""+str+"\\\"='\"+get"+headUpperBnm+"Yoyaku()"+"+\"'\";");
						}else{						
							outline2(outCsv2,"\t\t\tsSQL += \" \\\""+str+"\\\"='\"+get"+headUpperBnm+"()"+"+\"'\";");
						}
					}else{
						if(checkJavaYoyaku(str)){
							outline2(outCsv2,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"Yoyaku()"+"+\"'\";");
						}else{						
							outline2(outCsv2,"\t\t\tsSQL += \" "+str+"='\"+get"+headUpperBnm+"()"+"+\"'\";");
						}
					}
					if(str.equals("UPCNT") || str.equals("UPDTIME")){
						;
					}else{							
						outline2(outCsv2,"\t\t}");
					}
				}
				//△2020.09.02 sakurai 予約語対応
		}
		outline2(outCsv2,"\t\tsSQL += \" WHERE "+where+" \"+addWhere;");
		outline2(outCsv2,"\t\tsSQL = \"UPDATE "+csvtbl+" SET \" + sSQL;");
		outline2(outCsv2,"\t\tlog.info(sSQL);");
		outline2(outCsv2,"\t\tcond.setSQL(sSQL);");
		outline2(outCsv2,"\t\treturn dao.execute(cond);");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
		
		//insert句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")の作成");
		outline2(outCsv2,"\t * @param dao");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		String insert = "\tpublic int insert_"+methodName;
		insert += "(Dao dao) throws Exception {";
		outline2(outCsv2,insert);
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sColumns=\"\";");
		outline2(outCsv2,"");
		int count = 0;
		for(String str:koumoku2) {
			if(count > 0){
				String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
				if(str.equals("UPCNT") || str.equals("UPDTIME")){
					;
				}else{
					//▽2020.09.02 sakurai 予約語対応
//					outline2(outCsv2,"\t\tif (get"+headUpperBnm+"()!=null) {");
					if(checkJavaYoyaku(str)){
						outline2(outCsv2,"\t\tif (get"+headUpperBnm+"Yoyaku()!=null) {");
					}else{
						outline2(outCsv2,"\t\tif (get"+headUpperBnm+"()!=null) {");
					}
					//△2020.09.02 sakurai 予約語対応
				}
				//▽2020.10.19 sakurai Stringエラー対応
//				outline2(outCsv2,"\t\t\tif (sColumns != \"\") { sColumns+=\",\"; }");
				outline2(outCsv2,"\t\t\tif (!sColumns.equals(\"\")) { sColumns+=\",\"; }");
				//△2020.10.19 sakurai Stringエラー対応
				//▽2020.09.02 sakurai 予約語対応
//				outline2(outCsv2,"\t\t\tsColumns += \" "+str+"\"; ");
				if(checkOracleYoyaku(str)){
					outline2(outCsv2,"\t\t\tsColumns += \" \\\""+str+"\\\"\"; ");
					
				}else{
					outline2(outCsv2,"\t\t\tsColumns += \" "+str+"\"; ");
				}
				//△2020.09.02 sakurai 予約語対応
				if(str.equals("UPCNT") || str.equals("UPDTIME")){
					;
				}else{							
					outline2(outCsv2,"\t\t}");
				}
			}
			count++;
		}
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sValues=\"\";");
		outline2(outCsv2,"");
		//▽2020.7.14 sakurai IDを非表示にする。
//		for(String str:koumoku2) {
//			String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
//			outline2(outCsv2,"\t\tif (get"+headUpperBnm+"()!=null) {");
//			outline2(outCsv2,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
//			outline2(outCsv2,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"()"+"+\"'\";");
////			outline2(outCsv2,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"().replace(\"\\'\", \"\\\\'\")"+"+\"'\";");
//			outline2(outCsv2,"\t\t}");
//		}
		count = 0;
		for(String str:koumoku2) {
			if(count > 0){
				String headUpperBnm = str.toUpperCase().substring(0,1)+str.toLowerCase().substring(1);
				//▽2020.09.02 sakurai 予約語対応
//				outline2(outCsv2,"\t\tif (get"+headUpperBnm+"()!=null) {");
//				outline2(outCsv2,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
//				outline2(outCsv2,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"()"+"+\"'\";");
				if(checkJavaYoyaku(str)){					
					outline2(outCsv2,"\t\tif (get"+headUpperBnm+"Yoyaku()!=null) {");
					//▽2020.10.19 sakurai Stringエラー対応
//					outline2(outCsv2,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
					outline2(outCsv2,"\t\t\tif (!sValues.equals(\"\")) { sValues+=\",\"; }");
					//△2020.10.19 sakurai Stringエラー対応
					outline2(outCsv2,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"Yoyaku()"+"+\"'\";");
				}else{
					outline2(outCsv2,"\t\tif (get"+headUpperBnm+"()!=null) {");
					//▽2020.10.19 sakurai Stringエラー対応
//					outline2(outCsv2,"\t\t\tif (sValues != \"\") { sValues+=\",\"; }");
					outline2(outCsv2,"\t\t\tif (!sValues.equals(\"\")) { sValues+=\",\"; }");
					//△2020.10.19 sakurai Stringエラー対応
					outline2(outCsv2,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"()"+"+\"'\";");
				}
				//△2020.09.02 sakurai 予約語対応
	//			outline2(outCsv2,"\t\t\tsValues += \"'\"+get"+headUpperBnm+"().replace(\"\\'\", \"\\\\'\")"+"+\"'\";");
				outline2(outCsv2,"\t\t}");
				if(str.equals("UPCNT")){
					outline2(outCsv2,"\t\telse{");
					outline2(outCsv2,"\t\t\tif (!sValues.equals(\"\")) { sValues+=\",\"; }");
					outline2(outCsv2,"\t\t\tsValues += \"'0'\";");
					outline2(outCsv2,"\t\t}");
				}else if(str.equals("UPDTIME")){
					outline2(outCsv2,"\t\telse{");
					outline2(outCsv2,"\t\t\tif (!sValues.equals(\"\")) { sValues+=\",\"; }");
					outline2(outCsv2,"\t\t\tsValues += \"'\"+DateUtils.getNowTime()"+"+\"'\";");
					outline2(outCsv2,"\t\t}");
				}
			}
			count++;
		}
		//△2020.7.14 sakurai IDを非表示にする。
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sSQL=\"\";");
		//▽2020.7.14 sakurai IDを追加
//		outline2(outCsv2,"\t\tsSQL  = \"INSERT INTO "+csvtbl+" ( \"+sColumns;");
//		outline2(outCsv2,"\t\tsSQL += \" ) VALUES (\"+sValues;");
		outline2(outCsv2,"\t\tsSQL  = \"INSERT INTO "+csvtbl+" (" + csvtbl + "ID, \"+sColumns;");
		outline2(outCsv2,"\t\tsSQL += \" ) VALUES (" + csvtbl + "_ID_SEQ.NEXTVAL, \"+sValues;");
		//△2020.7.14 sakurai IDを追加
		outline2(outCsv2,"\t\tsSQL += \" ) \";");
		outline2(outCsv2,"\t\tlog.info(sSQL);");
		outline2(outCsv2,"\t\tcond.setSQL(sSQL);");
		outline2(outCsv2,"\t\treturn dao.execute(cond);");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
		
		//delete句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")の削除");
		outline2(outCsv2,"\t * @param dao");
		for(String str:param) {
			outline2(outCsv2,str);
		}
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		String delete = "\tpublic int delete_"+methodName;
		delete += "(Dao dao"+uke+") throws Exception {";
		outline2(outCsv2,delete);
		
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\tString sSQL = \"DELETE \";");
		outline2(outCsv2,"\t\tsSQL += \" FROM "+csvtbl.toUpperCase()+"\";");
		outline2(outCsv2,"\t\tsSQL += \" WHERE "+where+"\";");
		outline2(outCsv2,"\t\tlog.info(sSQL);");
		outline2(outCsv2,"\t\tcond.setSQL(sSQL);");
		outline2(outCsv2,"\t\treturn dao.execute(cond);");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
//		log.info("【"+methodName+"】　delete完成");
		//set句の作成
		//▽2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * "+tbljnm+"("+tblnm+")の設定");
		outline2(outCsv2,"\t * @param kmskk 値を保持しているtool");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		String set = "\tpublic " + methodName + "_tool set_"+methodName;
		set += "(" +methodName+"_tool inTool) throws Exception {";
		outline2(outCsv2,set);
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\t" + methodName + "_tool tmpTool = new "+ methodName + "_tool();");
		outline2(outCsv2,"");
		for(String str:setlist) {
			outline2(outCsv2,str);
		}
		outline2(outCsv2,"");
		outline2(outCsv2,"\t\treturn tmpTool;");
		outline2(outCsv2,"\t}");
		outline2(outCsv2,"");
		//△2020.09.07 sakurai listで複数行取ってnextで複数取得する際に新しくtoolをnewして全項目を移す必要があったので作成。
		
//		log.info("【"+methodName+"】　set完成");
		
		
		//初期化句の作成
		outline2(outCsv2,"\t/**");
		outline2(outCsv2,"\t * 初期化");
		outline2(outCsv2,"\t * @throws Exception");
		outline2(outCsv2,"\t */");
		outline2(outCsv2,"\tpublic void init() throws Exception {");
		for(String str:nasi) {
			outline2(outCsv2,"\t"+str);
		}
		outline2(outCsv2,"\t}");
		
//		log.info("【"+methodName+"】　初期化完成");
		
		
		for(String str:tter) {
			outline2(outCsv2,str);
		}
		
		outline2(outCsv2,"}");
		
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
		outCsv2.close();
		
//		log.info("【"+methodName+"】　END");
		
//		log.info("-------------------------------------------------------------------");
		
	}
	
	private void outline(PrintWriter outCsv, String str) throws Exception {
		byte[] buffer = str.getBytes("UTF-8");
		String content = new String(buffer, "UTF-8");
		outCsv.print(content);
		outCsv.print(System.getProperty("line.separator"));
		outCsv.flush();
		
	}
	
	private void outline2(CsvPrinter outCsv, String str) throws Exception {
		byte[] buffer = str.getBytes("UTF-8");
		String content = new String(buffer, "UTF-8");
//		byte[] buffer = str.getBytes("EUC-JP");
//		String content = new String(buffer, "EUC-JP");
		outCsv.addItem(content);
		outCsv.write();
	}
	

	
	/**
	 * テーブルCSV作成
	 * 
	 * @param outCsv csvライター
	 * @throws Exception
	 */
	private void makeTableCsv(PrintWriter outCsv, String csvtbl) throws Exception {
		
		//データベースを読み書きする準備
		RecordSet rs = null;
		Condition cond = new Condition();
		
		//検索SQLの作成
		String sql = "";
		
		//項目名の指定があり、テーブル項目マスタをJOINする場合
		sql = "";
		sql += " SELECT TBLNM,TBLNO,RNM,BNM,KATA,LNG1,LNG2,HSU,DFLT";
		sql += ",TKEY01,TKEY02,TKEY03,TKEY04,TKEY05,TKEY06,TKEY07";
		sql += ",TKEY08,TKEY09,TKEY10,TKEY11,TKEY12,TKEY13,TKEY14";
		sql += ",TKEY15,TKEY16,TKEY17,TKEY18,TKEY19,TKEY20,BIKO,UPCNT";
		sql += " FROM ZM_TBLITM";
		sql += " WHERE KC='"+kc+"' AND TBLNM='"+csvtbl+"'";
		sql += " ORDER BY TBLNM,TBLNO";
		
		cond.setSQL(sql);
//		log.info(sql);
		rs = dao.select(cond);
		int linecnt = 0;
		
		while (rs.next()) {
			
			//テーブル日本語名
			outCsv.print(rs.getStringValue("RNM")+",");
			
			//テーブル名
			outCsv.print(rs.getStringValue("BNM")+",");
			
			//型
			outCsv.print(rs.getStringValue("KATA")+",");
			
			//長さ１
			outCsv.print(rs.getStringValue("LNG1")+",");
			
			//長さ２
			outCsv.print(rs.getStringValue("LNG2")+",");
			
			//必須区分
			outCsv.print(rs.getStringValue("HSU")+",");
			
			//デフォルト値
			outCsv.print(rs.getStringValue("DFLT")+",");
			
			//一意区分
			outCsv.print(rs.getStringValue("TKEY01")+",");
			
			//備考
			outCsv.print(rs.getStringValue("BIKO")+",");
			
			outCsv.print(System.getProperty("line.separator"));
		    outCsv.flush();
		    
		    linecnt++;
		}
		
//		log.info(linecnt+"行出力しました");
		
		cond.clear();
		if (rs != null) {
			rs.close();
		}
		
	}

	/******************************************************************************************
	 *  Getter/Setter設定 Limy→全フィールドのGetter/Setterメソッド作成にて作成してください   *
	 ******************************************************************************************/
	
	/**
	 * Z101TblSyokaiListを取得します。
	 * @return Z101TblSyokaiList
	 */
	public List<Z101TblSyokaiBean> getZ101TblSyokaiList() {
	    return Z101TblSyokaiList;
	}

	/**
	 * Z101TblSyokaiListを設定します。
	 * @param Z101TblSyokaiList Z101TblSyokaiList
	 */
	public void setZ101TblSyokaiList(List<Z101TblSyokaiBean> Z101TblSyokaiList) {
	    this.Z101TblSyokaiList = Z101TblSyokaiList;
	}

	/**
	 * JSPへの引渡し情報　リスト表示区分を取得します。
	 * @return JSPへの引渡し情報　リスト表示区分
	 */
	public String getToggle() {
	    return toggle;
	}

	/**
	 * JSPへの引渡し情報　リスト表示区分を設定します。
	 * @param toggle JSPへの引渡し情報　リスト表示区分
	 */
	public void setToggle(String toggle) {
	    this.toggle = toggle;
	}

	/**
	 * JSPへの引渡し情報　リスト状況区分を取得します。
	 * @return JSPへの引渡し情報　リスト状況区分
	 */
	public String getJokyoKbn() {
	    return jokyoKbn;
	}

	/**
	 * JSPへの引渡し情報　リスト状況区分を設定します。
	 * @param jokyoKbn JSPへの引渡し情報　リスト状況区分
	 */
	public void setJokyoKbn(String jokyoKbn) {
	    this.jokyoKbn = jokyoKbn;
	}

	/**
	 * csv検索条件を取得します。
	 * @return csv検索条件
	 */
	public String getCsvKbn() {
	    return csvKbn;
	}

	/**
	 * csv検索条件を設定します。
	 * @param csvKbn csv検索条件
	 */
	public void setCsvKbn(String csvKbn) {
	    this.csvKbn = csvKbn;
	}

	/**
	 * 選択カラムNoを取得します。
	 * @return 選択カラムNo
	 */
	public String getColumNo() {
	    return columNo;
	}

	/**
	 * 選択カラムNoを設定します。
	 * @param columNo 選択カラムNo
	 */
	public void setColumNo(String columNo) {
	    this.columNo = columNo;
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
	 * 種別を取得します。
	 * @return 種別
	 */
	public String getShu() {
	    return shu;
	}

	/**
	 * 種別を設定します。
	 * @param shu 種別
	 */
	public void setShu(String shu) {
	    this.shu = shu;
	}

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
	 * カラム名を取得します。
	 * @return カラム名
	 */
	public String getClmnm() {
	    return clmnm;
	}

	/**
	 * カラム名を設定します。
	 * @param clmnm カラム名
	 */
	public void setClmnm(String clmnm) {
	    this.clmnm = clmnm;
	}

	/**
	 * カラム日本語名を取得します。
	 * @return カラム日本語名
	 */
	public String getClmjnm() {
	    return clmjnm;
	}

	/**
	 * カラム日本語名を設定します。
	 * @param clmjnm カラム日本語名
	 */
	public void setClmjnm(String clmjnm) {
	    this.clmjnm = clmjnm;
	}

	/**
	 * カラムリストを取得します。
	 * @return カラムリスト
	 */
	public boolean isClmList() {
	    return clmList;
	}

	/**
	 * カラムリストを設定します。
	 * @param clmList カラムリスト
	 */
	public void setClmList(boolean clmList) {
	    this.clmList = clmList;
	}
	
	/**
	 * Java予約語チェック
	 * 2020.09.02 sakurai
	 * 参考サイト：https://www.javadrive.jp/start/const/index7.html
	 * 
	 * @param key チェック対象のカラム名
	 * @throws Exception
	 */
	private boolean checkJavaYoyaku(String key) throws Exception {
		boolean ret = false;
		
		List<String> javaYoyakuList = new ArrayList<String>();
		javaYoyakuList.add("ABSTRACT");
		javaYoyakuList.add("ASSERT");
		javaYoyakuList.add("BOOLEAN");
		javaYoyakuList.add("BREAK");
		javaYoyakuList.add("BYTE");
		javaYoyakuList.add("CASE");
		javaYoyakuList.add("CATCH");
		javaYoyakuList.add("CHAR");
		javaYoyakuList.add("CLASS");
		javaYoyakuList.add("CONST");
		javaYoyakuList.add("CONTINUE");
		javaYoyakuList.add("DEFAULT");
		javaYoyakuList.add("DO");
		javaYoyakuList.add("DOUBLE");
		javaYoyakuList.add("ELSE");
		javaYoyakuList.add("ENUM");
		javaYoyakuList.add("EXTENDS");
		javaYoyakuList.add("FINAL");
		javaYoyakuList.add("FINALLY");
		javaYoyakuList.add("FLOAT");
		javaYoyakuList.add("FOR");
		javaYoyakuList.add("GOTO");
		javaYoyakuList.add("IF");
		javaYoyakuList.add("IMPLEMENTS");
		javaYoyakuList.add("IMPORT");
		javaYoyakuList.add("INSTANCEOF");
		javaYoyakuList.add("INT");
		javaYoyakuList.add("INTERFACE");
		javaYoyakuList.add("LONG");
		javaYoyakuList.add("NATIVE");
		javaYoyakuList.add("NEW");
		javaYoyakuList.add("PACKAGE");
		javaYoyakuList.add("PRIVATE");
		javaYoyakuList.add("PROTECTED");
		javaYoyakuList.add("PUBLIC");
		javaYoyakuList.add("RETURN");
		javaYoyakuList.add("SHORT");
		javaYoyakuList.add("STATIC");
		javaYoyakuList.add("STRICTFP");
		javaYoyakuList.add("SUPER");
		javaYoyakuList.add("SWITCH");
		javaYoyakuList.add("SYNCHRONIZED");
		javaYoyakuList.add("THIS");
		javaYoyakuList.add("THROW");
		javaYoyakuList.add("THROWS");
		javaYoyakuList.add("TRANSIENT");
		javaYoyakuList.add("TRY");
		javaYoyakuList.add("VOID");
		javaYoyakuList.add("VOLATILE");
		javaYoyakuList.add("WHILE");
		
		for(int i=0; i<javaYoyakuList.size(); i++){
			if(key.equals(javaYoyakuList.get(i))){
				ret = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * Oracle予約語チェック
	 * 2020.09.02 sakurai
	 * 参考サイト：https://docs.oracle.com/cd/E16338_01/appdev.112/b61344/appb.htm
	 * 
	 * @param key チェック対象のカラム名
	 * @throws Exception
	 */
	private boolean checkOracleYoyaku(String key) throws Exception {
		boolean ret = false;
		
		List<String> javaOracleList = new ArrayList<String>();
		javaOracleList.add("ABSTRACT");
		javaOracleList.add("ACCESS");
		javaOracleList.add("ELSE");
		javaOracleList.add("MODIFY");
		javaOracleList.add("START");
		javaOracleList.add("ADD");
		javaOracleList.add("EXCLUSIVE");
		javaOracleList.add("NOAUDIT");
		javaOracleList.add("SELECT");
		javaOracleList.add("ALL");
		javaOracleList.add("EXISTS");
		javaOracleList.add("NOCOMPRESS");
		javaOracleList.add("SESSION");
		javaOracleList.add("ALTER");
		javaOracleList.add("FILE");
		javaOracleList.add("NOT");
		javaOracleList.add("SET");
		javaOracleList.add("AND");
		javaOracleList.add("FLOAT");
		javaOracleList.add("NOTFOUND");
		javaOracleList.add("SHARE");
		javaOracleList.add("ANY");
		javaOracleList.add("FOR");
		javaOracleList.add("NOWAIT");
		javaOracleList.add("SIZE");
		javaOracleList.add("ARRAYLEN");
		javaOracleList.add("FROM");
		javaOracleList.add("NULL");
		javaOracleList.add("SMALLINT");
		javaOracleList.add("AS");
		javaOracleList.add("GRANT");
		javaOracleList.add("NUMBER");
		javaOracleList.add("SQLBUF");
		javaOracleList.add("ASC");
		javaOracleList.add("GROUP");
		javaOracleList.add("OF");
		javaOracleList.add("SUCCESSFUL");
		javaOracleList.add("AUDIT");
		javaOracleList.add("HAVING");
		javaOracleList.add("OFFLINE");
		javaOracleList.add("SYNONYM");
		javaOracleList.add("BETWEEN");
		javaOracleList.add("IDENTIFIED");
		javaOracleList.add("ON");
		javaOracleList.add("SYSDATE");
		javaOracleList.add("BY");
		javaOracleList.add("IMMEDIATE");
		javaOracleList.add("ONLINE");
		javaOracleList.add("TABLE");
		javaOracleList.add("CHAR");
		javaOracleList.add("IN");
		javaOracleList.add("OPTION");
		javaOracleList.add("THEN");
		javaOracleList.add("CHECK");
		javaOracleList.add("INCREMENT");
		javaOracleList.add("OR");
		javaOracleList.add("TO");
		javaOracleList.add("CLUSTER");
		javaOracleList.add("INDEX");
		javaOracleList.add("ORDER");
		javaOracleList.add("TRIGGER");
		javaOracleList.add("COLUMN");
		javaOracleList.add("INITIAL");
		javaOracleList.add("PCTFREE");
		javaOracleList.add("UID");
		javaOracleList.add("COMMENT");
		javaOracleList.add("INSERT");
		javaOracleList.add("PRIOR");
		javaOracleList.add("UNION");
		javaOracleList.add("COMPRESS");
		javaOracleList.add("INTEGER");
		javaOracleList.add("PRIVILEGES");
		javaOracleList.add("UNIQUE");
		javaOracleList.add("CONNECT");
		javaOracleList.add("INTERSECT");
		javaOracleList.add("PUBLIC");
		javaOracleList.add("UPDATE");
		javaOracleList.add("CREATE");
		javaOracleList.add("INTO");
		javaOracleList.add("RAW");
		javaOracleList.add("USER");
		javaOracleList.add("CURRENT");
		javaOracleList.add("IS");
		javaOracleList.add("RENAME");
		javaOracleList.add("VALIDATE");
		javaOracleList.add("DATE");
		javaOracleList.add("LEVEL");
		javaOracleList.add("RESOURCE");
		javaOracleList.add("VALUES");
		javaOracleList.add("DECIMAL");
		javaOracleList.add("LIKE");
		javaOracleList.add("REVOKE");
		javaOracleList.add("VARCHAR");
		javaOracleList.add("DEFAULT");
		javaOracleList.add("LOCK");
		javaOracleList.add("ROW");
		javaOracleList.add("VARCHAR2");
		javaOracleList.add("DELETE");
		javaOracleList.add("LONG");
		javaOracleList.add("ROWID");
		javaOracleList.add("VIEW");
		javaOracleList.add("DESC");
		javaOracleList.add("MAXEXTENTS");
		javaOracleList.add("ROWLABEL");
		javaOracleList.add("WHENEVER");
		javaOracleList.add("DISTINCT");
		javaOracleList.add("MINUS");
		javaOracleList.add("ROWNUM");
		javaOracleList.add("WHERE");
		javaOracleList.add("DROP");
		javaOracleList.add("MODE");
		javaOracleList.add("ROWSWITH");
		
		for(int i=0; i<javaOracleList.size(); i++){
			if(key.equals(javaOracleList.get(i))){
				log.info(key);
				ret = true;
			}
		}
		
		return ret;
	}

}
