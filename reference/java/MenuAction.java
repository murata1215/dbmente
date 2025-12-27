package jp.co.tisa.atg.common.menu;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateful;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import jp.co.tisa.atg.base.TablePageBeanBase;
import jp.co.tisa.atg.base.common.interceptor.PageBaseInterceptor;
import jp.co.tisa.atg.base.common.util.function.AtgUtils;
import jp.co.tisa.atg.base.common.util.function.LafitUtils;
import jp.co.tisa.atg.entity.EzZISYMN;
import jp.co.tisa.atg.entity.EzZMMENU;
import jp.co.tisa.atg.entity.EzZMMENUITM;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.faces.FacesMessages;

/**
 * メニューを表示する
 * <p>
 *
 * @author 小木曽
 */
@Stateful
@Name("menu")
@Interceptors(SeamInterceptor.class)
@PageBaseInterceptor
public class MenuAction extends TablePageBeanBase implements Menu {

	/**
	 * トップメニューID（共通）
	 */
	private static final String TOP_MENU_ID = "M0000";

	/**
	 * トップメニューID（本部）
	 */
	private static final String TOP_MENU_ID_HONBU = "MN010";

	/**
	 * トップメニューID（営業所）
	 */
	private static final String TOP_MENU_ID_EIGYOUSHO = "MN000";
	
	
	/**
	 * 管理者区分・一般ユーザー
	 */
	static final String CODE_KKB_NOR = "0";
	
	/**
	 * 管理者区分・管理者
	 */
	static final String CODE_KKB_ADM = "1";
	
	/**
	 * 管理者区分・システム管理者
	 */
	static final String CODE_KKB_SYS = "2";
	
	
	/**
	 * リンク先区分・タイトル
	 */
	static final String CODE_LKB_TITLE = "0";
	
	/**
	 * リンク先区分・メニューリンク
	 */
	static final String CODE_LKB_LINK = "1";
	
	/**
	 * リンク先区分・業務画面
	 */
	static final String CODE_LKB_VIEW = "2";
	
	/**
	 * リンク先区分・情報
	 */
	static final String CODE_LKB_HEADLINE = "3";
	
	/**
	 * 縦の最大行
	 */
	private static final int TATE_MAX = 25;
	
	/**
	 * 起動制限,起動OK
	 */
	private static final int KIDO_TRUE = 1;
	
	/**
	 * 起動制限,起動FALSE
	 */
	private static final int KIDO_FALSE = 2;
	
	/**
	 * 起動制限,マッチしなかった
	 */
	private static final int KIDO_NOMATCH = 3;
	
	/**
	 * ＲＤＥへのリンク
	 */
	private String rdelink="";
	
	/**
	 * 他プロジェクトへのリンクの自動押下
	 */
	private String otherProjectPush="";
	
	/**
	 * 他プロジェクトへのリンク
	 */
	private String otherProjectLink="";
	
	
	/**
	 * メニュー表示用クラス1<p>
	 * メニューデータ１画面75項目のうち、はじめの25件を取得する
	 */
	@DataModel
	private List<MenuActionDisp> menuDispList1;
	
	/**
	 * メニュー表示用クラス2<p>
	 * メニューデータ１画面60項目のうち、26件～50件を取得する
	 */
	@DataModel
	private List<MenuActionDisp> menuDispList2;
	
	/**
	 * メニュー表示用クラス3<p>
	 * メニューデータ１画面60項目のうち、51件～75件を取得する
	 */
	@DataModel
	private List<MenuActionDisp> menuDispList3;
	
	/**
	 * メニュー表示用クラス1を押下されたら<p>
	 * 押下した情報が格納されるクラス
	 */
	@DataModelSelection("menuDispList1")
	private MenuActionDisp zm_menu_disp1;
	
	/**
	 * メニュー表示用クラス2を押下されたら<p>
	 * 押下した情報が格納されるクラス
	 */
	@DataModelSelection("menuDispList2")
	private MenuActionDisp zm_menu_disp2;
	
	/**
	 * メニュー表示用クラス3を押下されたら<p>
	 * 押下した情報が格納されるクラス
	 */
	@DataModelSelection("menuDispList3")
	private MenuActionDisp zm_menu_disp3;
	
	/**
	 * facesmessage<p>
	 * メニューのエラー等を表示する
	 */
	@In(create=true)
	private transient FacesMessages facesMessages;

	/**
	 * タイトル
	 */
	private String title;

	/**
	 * 従業員コード
	 */
	private String sycd;
	
	/**
	 * 管理者区分
	 */
	private String kkb;
	
	/**
	 * メニューID
	 */
	private String menuId;
	
	/**
	 * コンストラクタ
	 * <p>
	 * 
	 * @throws Exception
	 */
	public MenuAction() throws Exception {
		super();
		setMainTitle("メインメニュー");
		setScreenId("MENU");
		setFacesId("menu");
	}

	/**
	 * 初期処理
	 * <br>
	 * ・初期状態を表示
	 */
	@Override
	public String subInit() throws Exception {
		
		//項目初期化
		getEntityManager().clear();
		makeTitle();
		
		return "menu";
	}
	
	/**
	 * タイトルの作成を行います<p>
	 * menu.jspが呼ばれる毎に当メソッドは呼び出される<p>
	 * pages.xmlに定義<p>
	 */
	public void makeTitle() {
		
		setHbSet(true);
		//ログイン情報よりメニューＩＤの設定
		if (menuId == null || kkb == null) {
			log.info("ログイン情報よりメニューＩＤを設定します");
			searchSymn();
	        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
			// 部門変更中の場合はメニューＩＤを上書き
			/*
			if(getLoginBean().isWsc()){
				menuId = topMenuId();
			}
			*/
			// △Modify 2010/10/20 Jinno	業務部門変更機能対応

			if (AtgUtils.checkMust(getLoginBean().getGid())) {
				setMenuId(getLoginBean().getGid());
				getLoginBean().setGid("");
			}
		}
		
		String hql = "";
		Query query = null;
		//トップメニューの場合
		if (TOP_MENU_ID.equals(menuId)) {
			
			//HQLを作成する。
			hql = "from EzZMMENU where kc = :kc and mid = :mid and lkb = :lkb";
			
			//クエリオブジェクトを作成する。
			query = getEntityManager().createQuery(hql);
			
			//クエリにパラメータをセットする。
	        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
			//query.setParameter("kc", getLoginBean().getKc());
			// △Modify 2010/10/20 Jinno	業務部門変更機能対応
			query.setParameter("kc", getLoginBean().getGkc());
			query.setParameter("mid", menuId);
			query.setParameter("lkb", "0");

		//それ以外
		} else {
			//HQLを作成する。
			hql = "from EzZMMENU where kc = :kc and mid = (select upid from EzZMMENU where kc = :kc2 and mid = :mid and pno = :pno) and lid = :lid";
			
			//クエリオブジェクトを作成する。
			query = getEntityManager().createQuery(hql);
			
			//クエリにパラメータをセットする。
	        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
			//query.setParameter("kc", getLoginBean().getKc());
			//query.setParameter("kc2", getLoginBean().getKc());
			// △Modify 2010/10/20 Jinno	業務部門変更機能対応
			query.setParameter("kc", getLoginBean().getGkc());
			query.setParameter("kc2", getLoginBean().getGkc());
	   		query.setParameter("mid", menuId);
			query.setParameter("lid", menuId);
			query.setParameter("pno", new Long("1"));
		}
		
		//メニューマスタを検索する。
		EzZMMENU result = null;
		
		try{
			result = (EzZMMENU)query.getSingleResult();
			//タイトルの取得
			title = result.getMtitle();

		}catch(NoResultException nre){
			//結果が0件の場合
			title = "";
			
		}catch(NonUniqueResultException nure){
			throw new RuntimeException("複数の結果が抽出されました。", nure);
		}
		
		rdelink ="http://"+AtgUtils.getIPaddress("lfgpt1");
		rdelink+=":44090/ReportDirector/rdservlet?mode=autologin&username=";
		rdelink+=getLoginBean().getSycd()+"&password="+getLoginBean().getSycd()+"&userinfo=";
		
		Contexts.getSessionContext().set("menuid", menuId);

		makeMenu();
	}
	
	/**
	 * メニューリストの作成を行う
	 */
    @SuppressWarnings("unchecked")
	private void makeMenu() {

		getEntityManager().clear();
		
		//HQLを作成する。
		String hql = "from EzZMMENU where kc = :kc and mid = :mid order by pno";
		
		//クエリオブジェクトを作成する。
		Query query = getEntityManager().createQuery(hql);
		
		//クエリにパラメータをセットする。
        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		//query.setParameter("kc", getLoginBean().getKc());
		query.setParameter("kc", getLoginBean().getGkc());
        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
		query.setParameter("mid", menuId);

		//メニューマスタ検索
		List<EzZMMENU> menu_list = query.getResultList();
		
		//トップメニューの場合、管理者区分により表示メニューの切り替え
		if (TOP_MENU_ID.equals(menuId)) {
			
			//システム管理者の場合
			if (CODE_KKB_SYS.equals(kkb)) {}

			//管理者の場合
			if (CODE_KKB_ADM.equals(kkb))
				for (int i = 0; i < 1; i++)
					menu_list.remove(3);
			
			//一般ユーザーの場合
			if (CODE_KKB_NOR.equals(kkb) || AtgUtils.checkMust(kkb) == false) {
		        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
				//if (getLoginBean().getHbkb().equals("0")) {
				if (getLoginBean().getGhbkb().equals("0")) {
		        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
					//営業所スタッフの場合
					menu_list.remove(3);
					menu_list.remove(2);
					menu_list.remove(1);
				} else {
					//本部スタッフの場合
					menu_list.remove(3);
					menu_list.remove(2);
				}
			}
			
		}
		
		//メニューマスタの件数が０件の場合
		if (menu_list.size() == 0) {
			facesMessages.add("メニューマスタのデータ不整合です。");
		}
		
		//表示用リストの作成
		menuDispList1 = new ArrayList<MenuActionDisp>();
		menuDispList2 = new ArrayList<MenuActionDisp>();
		menuDispList3 = new ArrayList<MenuActionDisp>();
		
		List<MenuActionPack> menuPack = new ArrayList<MenuActionPack>();
		
		EzZMMENUITM menuitm = null;
		String project = "";
		String stlimit = "";
		String staddress = "";
		String stparam = "";
		String alt = "";
		
		//メニューマスタの件数分ループ
		for (EzZMMENU menuObj : menu_list) {
			
			boolean ac = true;
			
			//メニュー項目マスタの検索
			try {
				
				//リンク先区分='2'の場合、メニュー項目マスタを検索
		        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
				/*
				menuitm = (EzZMMENUITM) getEntityManager().createQuery ("from EzZMMENUITM where kc = :kc and gid = :gid")
				.setParameter("kc", getLoginBean().getKc())
				.setParameter("gid", menuObj.getLid())
				.getSingleResult();
				*/
				menuitm = (EzZMMENUITM) getEntityManager().createQuery ("from EzZMMENUITM where kc = :kc and gid = :gid")
				.setParameter("kc", getLoginBean().getGkc())
				.setParameter("gid", menuObj.getLid())
				.getSingleResult();
		        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
				
				//プロジェクト名称の取得
				project = menuitm.getProject();
				
				//起動制限の取得
				stlimit = menuitm.getStlimit();
				
				//起動アドレスの取得
				staddress = menuitm.getStaddress();
				
				//稼動前・未設定の場合は業務制限をかける
				if (staddress != null) {
					if (staddress.equals("#{XXXXXXXXXXX.init}")) {
						stlimit = "1";
					}
				}
				
				//ユーザー・端末・部門毎の起動制限の取得
				ac = acCheck(	menuitm.getAcusr(),
								menuitm.getActm(),
								menuitm.getAcbu(),
								getLoginBean().getSycd(),
								getLoginBean().getClnm(),
								getLoginBean().getGbu());
				
				//起動パラメータの取得
				stparam = menuitm.getStparam();
				
				//画面説明の取得
				alt = menuitm.getAlt();
				
			//メニュー項目マスタの検索結果が０件の場合
			} catch (NoResultException nre) {
				stlimit = "";
				staddress = "";
			}
			
			MenuActionPack pack = new MenuActionPack();
			
			pack.setMid(menuObj.getMid());
			pack.setPno(menuObj.getPno());
			pack.setMtitle(menuObj.getMtitle());
			pack.setAlt(alt);
			pack.setLkb(menuObj.getLkb());
			pack.setLid(menuObj.getLid());
			pack.setProject(project);
			pack.setParam(stparam);
			pack.setStlimit(stlimit);
			pack.setStaddress(staddress);

			// 起動制限のチェックがOKならば
			if (ac == true) {
				//表示用リストへの格納
				menuPack.add(pack);
			}
			
		}

		//メニューマスタの件数分ループ
		for (int i = 0; i < TATE_MAX; i++) {
			menuDispList1.add(setMenuBlank((new MenuActionDisp()), i));
			menuDispList2.add(setMenuBlank((new MenuActionDisp()), i));
			menuDispList3.add(setMenuBlank((new MenuActionDisp()), i));
		}
		
		// 表示用の menuDispList へ 取得した menuPack を順番に格納する
		for (MenuActionPack map : menuPack) {
			int cnt = (int) map.getPno();
			
			// 1列目の処理
			if (cnt < TATE_MAX) {
				setMenuDispList(menuDispList1.get(cnt), map);
			}
			
			// 2列目の処理
			if (cnt < TATE_MAX * 2 && cnt >= TATE_MAX) {
				setMenuDispList(menuDispList2.get(cnt - TATE_MAX), map);
			}
			
			// 3列目の処理
			if (cnt < TATE_MAX * 3 && cnt > TATE_MAX * 2) {
				setMenuDispList(menuDispList3.get(cnt - TATE_MAX * 2), map);
			}
			
		}
			
		
		hql = "";
	}
    
    /**
     * 起動制限情報のチェック
     * 
     * @param acusr ユーザーチェックテキスト
     * @param actm 端末チェックテキスト
     * @param acbu 部門チェックテキスト
     * @param usr ユーザー
     * @param tm 端末
     * @param bu 部門
     * @return boolean 制限なし時はtrue
     */
    private boolean acCheck(String acusr, String actm, String acbu, String usr, String tm, String bu) {
    	boolean ret = true;
    	
    	int retBu = ptnCheck(acbu,bu);
    	int retUsr = ptnCheck(acusr,usr);
    	int retTm = ptnCheck(actm,tm);
    	
    	if (retBu == KIDO_TRUE )
    		ret = true;
    	if (retBu == KIDO_FALSE )
    		ret = false;
    	
    	if (retTm == KIDO_TRUE )
    		ret = true;
    	if (retTm == KIDO_FALSE )
    		ret = false;
    	
    	if (retUsr == KIDO_TRUE )
    		ret = true;
    	if (retUsr == KIDO_FALSE )
    		ret = false;
    	
    	return ret;
    }
    
    /**
     * 起動制限情報を参照し、起動制限フラグを返す
     * 
     * @param acstr 起動制限パターン
     * @param str 比較文字列
     * @return boolean 起動可=true, 起動不可=false
     */
    static int ptnCheck(String acstr, String str) {
    	
    	int ret = KIDO_NOMATCH;
    	
    	if ((acstr == null || acstr == "")) {
    		
    		// 何もしない
    		;
    		
    	} else {
    		
	    	Pattern pattern = null;
	    	Matcher matcher = null;
	    	
			// , SPACEで分割
	    	pattern = Pattern.compile(",+");
	    	String[] strs = pattern.split(acstr);
	    	
	    	for (int i=0;i<strs.length;i++) {
	    		
	    		// OK=のエリアかどうかをチェック
	        	pattern = Pattern.compile("^OK=.*");
	        	matcher = pattern.matcher(strs[i]);
	        	boolean okmatch = matcher.matches();
	        	
	        	// 起動OKのものを取り出す
	        	if (okmatch == true) {
	        		
	        		// OK=を削除
	        		pattern = Pattern.compile("OK=");
	        		matcher = pattern.matcher(strs[i]);
	        		String ok = matcher.replaceFirst("");
	            	
	        		// ALL（ワイルドカード）にマッチするか？
	        		pattern = Pattern.compile(".*ALL.*");
	            	matcher = pattern.matcher(ok);
	            	boolean matchAll = matcher.matches();
	            	
	            	if (matchAll == false) {
	            		// 入力値とパターンマッチを行う
	            		pattern = Pattern.compile(".*"+str+".*");
	                	matcher = pattern.matcher(ok);
	                	boolean match = matcher.matches();
	                	
	                	if (match == true) {
	                		ret = KIDO_TRUE;
	                	}
	            	} else {
	            		// ALL（ワイルドカード）にマッチしたら、すべてOK
	            		ret = KIDO_TRUE;
	            	}
	        	}
	    		
	    		// NG=のエリアかどうかをチェック
	        	pattern = Pattern.compile("^NG=.*");
	        	matcher = pattern.matcher(strs[i]);
	        	boolean ngmatch = matcher.matches();
	        	
	        	// 起動OKのものを取り出す
	        	if (ngmatch == true) {
	        		
	        		// NG=を削除
	        		pattern = Pattern.compile("NG=");
	        		matcher = pattern.matcher(strs[i]);
	        		String ng = matcher.replaceFirst("");
	            	
	        		// ALL（ワイルドカード）にマッチするか？
	        		pattern = Pattern.compile(".*ALL.*");
	            	matcher = pattern.matcher(ng);
	            	boolean matchAll = matcher.matches();
	            	
	            	if (matchAll == false) {
	            		// 入力値とパターンマッチを行う
	            		pattern = Pattern.compile(".*"+str+".*");
	                	matcher = pattern.matcher(ng);
	                	boolean match = matcher.matches();
	                	
	                	if (match == true) {
	                		ret = KIDO_FALSE;
	                	}
	            	} else {
	            		// ALL（ワイルドカード）にマッチしたら、すべてOK
	            		ret = KIDO_FALSE;
	            	}
	        	}
	    	}
	    	
    	}
    	
    	return ret;
    }
    
    /**
     * メニュー情報の初期化<p>
     * @param disp MenuActionDispの初期化するクラス情報
     * @param idx 初期化する場合の場所番号
     * @return MenuActionDispの初期化後の情報
     */
    static MenuActionDisp setMenuBlank(MenuActionDisp disp, int idx) {
		disp.setMid("");
		disp.setPno(idx);
		disp.setMtitle("　");
		disp.setAlt("　");
		disp.setLkb("");
		disp.setLid("");
		disp.setStlimit("");
		disp.setStaddress("");
		String[] st = new String[10];
		for (int j=0;j<10;j++){
			st[j]="p";
		}
		disp.setParamName(st);
		disp.setParamValue(st);
		return disp;
    }
	
    /**
     * メニュー情報のコピー<p>
     * @param dist MenuActionDispのコピー先
     * @param src  MenuActionDispのコピー元
     */
    static void setMenuDispList(MenuActionDisp dist, MenuActionPack src) {
		dist.setMid(src.getMid());
		dist.setPno(src.getPno());
		dist.setMtitle(src.getMtitle());
		dist.setAlt(src.getAlt());
		dist.setLkb(src.getLkb());
		dist.setLid(src.getLid());
		dist.setStlimit(src.getStlimit());
		dist.setProject(src.getProject());
		dist.setStaddress(src.getStaddress());
		dist.setParam(src.getParam());
		dist.setParamName(AtgUtils.getParamName(src.getParam()));
		dist.setParamValue(AtgUtils.getParamValue(src.getParam()));
    }
    
	/**
	 * リンク先の設定を行います.
	 * @return リンク先画面
	 */
	public String link() {
		
		return link_jump(zm_menu_disp1);
	}
	
	/**
	 * リンク先の設定を行います（２列目）.
	 * @return リンク先画面
	 */
	public String link2() {
		
		return link_jump(zm_menu_disp2);
	}
	
	/**
	 * リンク先の設定を行います（３列目）.
	 * @return リンク先画面
	 */
	public String link3() {
		
		return link_jump(zm_menu_disp3);
	}
	
	private String link_jump(MenuActionDisp mad) {
		
		return link_sub(
					mad.getLkb(),
					mad.getLid(),
					mad.getMtitle(),
					mad.getStaddress(),
					mad.getProject(),
					mad.getParam());
	}
	
	/**
	 * リンク押下時の処理を行う<p>
	 * @param lkb リンク先区分
	 * @param lid リンク先画面ID
	 * @param mtitle リンク先画面タイトル
	 * @param sta リンク先アドレス
	 * @param prj リンク先プロジェクト
	 * @param prm パラメータ
	 * @return リンク先画面ID
	 */
	private String link_sub(String lkb, String lid, String mtitle, String sta, String prj, String prm) {
		
		String link = "";
		
		//リンク先区分='1'(リンク先取得)の場合
		if (CODE_LKB_LINK.equals(lkb)) {
			//メニューIDの取得
			setMenuId(lid);
			//メニューの組み立て
			makeMenu();
			//リンク先:メニュー画面
			link = "menu";
			log.info("「"+mtitle+"」のリンクを取得");
		
		//リンク先区分='2'(業務画面立ち上げ)の場合
		} else if (CODE_LKB_VIEW.equals(lkb)) {

//			if (prj.equals(getLoginBean().getProjectName())) {
//				//自プロジェクト内のリンクだった場合
//				MenuSub ms = new MenuSub();
//				link = ms.link_exec(getEntityManager(), getLoginBean(), lkb, lid, mtitle, sta, log);
//			} else {
				
/*				//他プロジェクトへのリンクだった場合
				//localhost:8080/atg_project1/Common/autoLogin.seam?85,97010,CHILD,project1.init
				String staddress = sta.replace("#{", "");
				staddress = staddress.replace("}", "");
				
				//リンク先の組み立て
				String olink = "http://";
				olink += AtgUtils.getIPaddress(prj);
				olink += ":8080/";
				olink += prj;
				olink += "/Common/autoLogin.seam?";
				olink += getLoginBean().getKc()+",";
				olink += getLoginBean().getSycd()+",";
				olink += "CHILD"+",";
				olink += staddress+",";
				if (AtgUtils.checkMust(prm)) {
					olink += prm+",";
				}
				otherProjectPush = "1";
				otherProjectLink = olink;
				log.info("「"+olink+"」のリンクを作成");
*/				
				getLoginBean().setNextmid(lid);
//				getLoginBean().setNextparam(sta);
				getLoginBean().setNextparam(prm);
				getLoginBean().setRtnmid(menuId);
				getLoginBean().setRtnparam("");
				
				link="nextLink";
//			}
			
		}
		
		return link;
		
	}

	/**
	 * 戻るボタン押下時の処理を行います<p>
	 * @return リンク先画面
	 */
	public String back() {
		
		// 不整合等が起きた場合は、トップメニューへ遷移する
		if (menuId == null || menuId == "") {
			
			setMenuId(topMenuId());
			
		}

		//上位メニューID
		String upid = "";
		//HQLを作成する。
		String hql = "from EzZMMENU where kc = :kc and mid = :mid and pno = :pno";
		
		//クエリオブジェクトを作成する。
		Query query = getEntityManager().createQuery(hql);
		
		//クエリにパラメータをセットする。
        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		//query.setParameter("kc", getLoginBean().getKc());
		query.setParameter("kc", getLoginBean().getGkc());
        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
   		query.setParameter("mid", menuId);
		query.setParameter("pno", new Long("1"));
	
		//メニューマスタを検索する。
		EzZMMENU result = null;
		
		try{
			result = (EzZMMENU)query.getSingleResult();
			//上位メニューIDの取得
			upid = result.getUpid();
	
		}catch(NoResultException nre){
			facesMessages.add("メニューマスタのデータ不整合です。");
			
		}catch(NonUniqueResultException nure){
			throw new RuntimeException("複数の結果が抽出されました。", nure);
		}
		
		//メニューIDの退避
		setMenuId(upid);
		
		//タイトルの設定
		makeTitle();
		//メニューの設定
		makeMenu();
		
		return "menu";
	}
	
	/**
	 * 権限毎のトップメニューＩＤを返す
	 * @return トップメニューのＩＤ
	 */
	private String topMenuId() {
		
		String ret="";
		
		//システム管理者の場合
		if (CODE_KKB_SYS.equals(kkb)) {
			ret = TOP_MENU_ID;
			
		}

		//管理者の場合
		if (CODE_KKB_ADM.equals(kkb)) {
			ret = TOP_MENU_ID;
			
		}
		
		//一般ユーザーの場合
		if (CODE_KKB_NOR.equals(kkb) || AtgUtils.checkMust(kkb) == false) {
	        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
			//if (getLoginBean().getHbkb().equals("0")) {
			if (getLoginBean().getGhbkb().equals("0")) {
	        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
				//営業所スタッフの場合
				ret = TOP_MENU_ID_EIGYOUSHO;
			} else {
				//本部スタッフの場合
				ret = TOP_MENU_ID_HONBU;
			}
		}
		
		return ret;
	}
	
	
	/**
	 * 戻るボタン表示可否の判定を行います.
	 * @return 表示可否 (true:表示可 false:表示不可)
	 */
	public boolean isAvailableBack() {
		
		boolean result = true;
		
		//トップメニューの場合、非表示
		if (menuId.equals(topMenuId())) {
			result = false;
		}
		return result;
	}
	
	/**
	 * 従業員マスタの検索を行います.
	 */
	private void searchSymn() {
		
		//会社コード、従業員コードをログイン情報より取得
		sycd = getLoginBean().getSycd();
		
		//HQLを作成する。
		String hql = "from EzZISYMN where kc = :kc and sycd = :sycd";
		
		//クエリオブジェクトを作成する。
		Query query = getEntityManager().createQuery(hql);
		
		//クエリにパラメータをセットする。
		query.setParameter("kc", getLoginBean().getKc());
		query.setParameter("sycd", sycd);
		
		try{
			//社員メニューマスタを検索する。
			EzZISYMN result = (EzZISYMN)query.getSingleResult();
			//メニューIDの取得
			if (menuId == null)
				setMenuId(result.getMid());
			
			//管理者区分の取得
			kkb = result.getKnkb();
			
			//メニューIDが取得できなかった場合
			if (menuId == null || menuId.length() == 0) {
				
				setMenuId(topMenuId());
			}

		}catch(NoResultException nre){
			//設定なし
			if (menuId == null)
				setMenuId(topMenuId());
			if (kkb == null)
				kkb = "";
			
		}catch(NonUniqueResultException nure){
			
			throw new RuntimeException("複数の結果が抽出されました。", nure);
		}
	}
	
	/*
	 * PageBeanをオーバーライドして、ヘッダー情報設定を制御する。
	 * link時は、ヘッダを設定しない。
	 * <p>
	 * @param method メソッド
	 * @throws 例外
	 */
	@Override
	protected void subPostInvoke(Method method) throws Exception {
		
		if ("link".equals(method.getName())
			|| "link2".equals(method.getName())
			|| "link3".equals(method.getName()) 
			|| "linkInit".equals(method.getName())) 
		{
			setHbSet(false);
		} else {
			setHbSet(true);
		}
		
		super.subPostInvoke(method);
	}

	/**
	 * ＲＤＥへのリンクを取得します。
	 * @return ＲＤＥへのリンク
	 */
	public String getRdelink() {
	    return rdelink;
	}

	/**
	 * ＲＤＥへのリンクを設定します。
	 * @param rdelink ＲＤＥへのリンク
	 */
	public void setRdelink(String rdelink) {
	    this.rdelink = rdelink;
	}

	/**
	 * 他プロジェクトへのリンクの自動押下を取得します。
	 * @return 他プロジェクトへのリンクの自動押下
	 */
	public String getOtherProjectPush() {
	    return otherProjectPush;
	}

	/**
	 * 他プロジェクトへのリンクの自動押下を設定します。
	 * @param otherProjectPush 他プロジェクトへのリンクの自動押下
	 */
	public void setOtherProjectPush(String otherProjectPush) {
	    this.otherProjectPush = otherProjectPush;
	}

	/**
	 * 他プロジェクトへのリンクを取得します。
	 * @return 他プロジェクトへのリンク
	 */
	public String getOtherProjectLink() {
	    return otherProjectLink;
	}

	/**
	 * 他プロジェクトへのリンクを設定します。
	 * @param otherProjectLink 他プロジェクトへのリンク
	 */
	public void setOtherProjectLink(String otherProjectLink) {
	    this.otherProjectLink = otherProjectLink;
	}

	/**
	 * メニュー表示用クラス1<p>を取得します。
	 * @return メニュー表示用クラス1<p>
	 */
	public List<MenuActionDisp> getMenuDispList1() {
	    return menuDispList1;
	}

	/**
	 * メニュー表示用クラス1<p>を設定します。
	 * @param menuDispList1 メニュー表示用クラス1<p>
	 */
	public void setMenuDispList1(List<MenuActionDisp> menuDispList1) {
	    this.menuDispList1 = menuDispList1;
	}

	/**
	 * メニュー表示用クラス2<p>を取得します。
	 * @return メニュー表示用クラス2<p>
	 */
	public List<MenuActionDisp> getMenuDispList2() {
	    return menuDispList2;
	}

	/**
	 * メニュー表示用クラス2<p>を設定します。
	 * @param menuDispList2 メニュー表示用クラス2<p>
	 */
	public void setMenuDispList2(List<MenuActionDisp> menuDispList2) {
	    this.menuDispList2 = menuDispList2;
	}

	/**
	 * メニュー表示用クラス3<p>を取得します。
	 * @return メニュー表示用クラス3<p>
	 */
	public List<MenuActionDisp> getMenuDispList3() {
	    return menuDispList3;
	}

	/**
	 * メニュー表示用クラス3<p>を設定します。
	 * @param menuDispList3 メニュー表示用クラス3<p>
	 */
	public void setMenuDispList3(List<MenuActionDisp> menuDispList3) {
	    this.menuDispList3 = menuDispList3;
	}

	/**
	 * メニュー表示用クラス1を押下されたら<p>を取得します。
	 * @return メニュー表示用クラス1を押下されたら<p>
	 */
	public MenuActionDisp getZm_menu_disp1() {
	    return zm_menu_disp1;
	}

	/**
	 * メニュー表示用クラス1を押下されたら<p>を設定します。
	 * @param zm_menu_disp1 メニュー表示用クラス1を押下されたら<p>
	 */
	public void setZm_menu_disp1(MenuActionDisp zm_menu_disp1) {
	    this.zm_menu_disp1 = zm_menu_disp1;
	}

	/**
	 * メニュー表示用クラス2を押下されたら<p>を取得します。
	 * @return メニュー表示用クラス2を押下されたら<p>
	 */
	public MenuActionDisp getZm_menu_disp2() {
	    return zm_menu_disp2;
	}

	/**
	 * メニュー表示用クラス2を押下されたら<p>を設定します。
	 * @param zm_menu_disp2 メニュー表示用クラス2を押下されたら<p>
	 */
	public void setZm_menu_disp2(MenuActionDisp zm_menu_disp2) {
	    this.zm_menu_disp2 = zm_menu_disp2;
	}

	/**
	 * メニュー表示用クラス3を押下されたら<p>を取得します。
	 * @return メニュー表示用クラス3を押下されたら<p>
	 */
	public MenuActionDisp getZm_menu_disp3() {
	    return zm_menu_disp3;
	}

	/**
	 * メニュー表示用クラス3を押下されたら<p>を設定します。
	 * @param zm_menu_disp3 メニュー表示用クラス3を押下されたら<p>
	 */
	public void setZm_menu_disp3(MenuActionDisp zm_menu_disp3) {
	    this.zm_menu_disp3 = zm_menu_disp3;
	}

	/**
	 * facesmessage<p>を取得します。
	 * @return facesmessage<p>
	 */
	public FacesMessages getFacesMessages() {
	    return facesMessages;
	}

	/**
	 * facesmessage<p>を設定します。
	 * @param facesMessages facesmessage<p>
	 */
	public void setFacesMessages(FacesMessages facesMessages) {
	    this.facesMessages = facesMessages;
	}

	/**
	 * タイトルを取得します。
	 * @return タイトル
	 */
	public String getTitle() {
	    return title;
	}

	/**
	 * タイトルを設定します。
	 * @param title タイトル
	 */
	public void setTitle(String title) {
	    this.title = title;
	}

	/**
	 * 従業員コードを取得します。
	 * @return 従業員コード
	 */
	public String getSycd() {
	    return sycd;
	}

	/**
	 * 従業員コードを設定します。
	 * @param sycd 従業員コード
	 */
	public void setSycd(String sycd) {
	    this.sycd = sycd;
	}

	/**
	 * 管理者区分を取得します。
	 * @return 管理者区分
	 */
	public String getKkb() {
	    return kkb;
	}

	/**
	 * 管理者区分を設定します。
	 * @param kkb 管理者区分
	 */
	public void setKkb(String kkb) {
	    this.kkb = kkb;
	}

	/**
	 * メニューIDを取得します。
	 * @return メニューID
	 */
	public String getMenuId() {
	    return menuId;
	}

	/**
	 * メニューIDを設定します。
	 * @param menuId メニューID
	 */
	public void setMenuId(String menuId) {
	    this.menuId = menuId;
	    getLoginBean().setMid(menuId);
	}
	
}
