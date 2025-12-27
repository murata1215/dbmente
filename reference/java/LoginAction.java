//$Id: LoginAction.java,v 1.1 2006/10/17 04:06:15 cvsrepo Exp $
package jp.co.tisa.atg.common.login;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import jp.co.tisa.atg.base.SessionBeanBase;
import jp.co.tisa.atg.base.common.LoginBean;
import jp.co.tisa.atg.base.common.LoginBeanBase;
import jp.co.tisa.atg.base.common.util.LogManager;
import jp.co.tisa.atg.base.common.util.function.AtgUtils;
import jp.co.tisa.atg.base.common.util.function.CSVTokenizer;
import jp.co.tisa.atg.base.common.util.function.LafitUtils;
import jp.co.tisa.atg.common.menu.MenuSub;
import jp.co.tisa.atg.entity.EzZIBUMST;
import jp.co.tisa.atg.entity.EzZISESSION;
import jp.co.tisa.atg.entity.EzZISYS;
import jp.co.tisa.atg.entity.EzZIUSER;
import jp.co.tisa.atg.entity.EzZMCOMPANY;
import jp.co.tisa.atg.entity.EzZMMENUITM;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.web.Session;

/**
 * ログインクラス
 * <p>
 * @author 村田
 */
@Stateful
@Name("login")
@Scope(ScopeType.SESSION)
@Interceptors(SeamInterceptor.class)
public class LoginAction extends SessionBeanBase implements Login
{
	/**
	 * パラメータ
	 */
	@In(required=false)
	private String loginParam;

	/**
	 * メッセージ
	 */
	@In(create=true)
	private transient FacesMessages facesMessages;
	
	/**
	 * ヘッダコマンド
	 */
	private String command;
	
	/**
     * 会社コード
     */
    private String kc;
    
    // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
    /**
     * 業務会社コード
     */
    private String gkc;
    
    /**
     * 業務部門コード
     */
    private String gbu;
    // △Modify 2010/10/20 Jinno	業務部門変更機能対応
    
    /**
     * ユーザＩＤ
     */
    private String user;
    
    /**
     * パスワード
     */
    private String pass;
    
    /**
     * セッションカウント
     */
    private Long sescnt;
    
    /**
     * 起動情報
     */
    private String kidoInfo;
	
	/**
	 * 自動押下用フラグ
	 */
	private String loginAutoPush;
	
	/**
	 * 初期起動メソッド名
	 */
	private String loginParamMenu;
	
	/**
	 * 初期起動パラメタ名
	 */
	private String[] loginParamParam = new String[10];
	
	/**
	 * 初期起動パラメタ値
	 */
	private String[] loginParamValue = new String[10];
	
	/**
	 * 画面ＩＤ
	 */
	private String gid;
	
    // ▽Modify 2011/01/18 Jinno	画面間メッセージ対応
	/**
	 * 業務間メッセージ
	 */
	private String message;
    // △Modify 2010/01/18 Jinno	画面間メッセージ対応

	// ▽ Start  追加    2011/09/13    jinno
	// ユーザー・端末・部門毎の起動制限処理
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
	// △ End    追加    2011/09/13    jinno
	
	/**
	 * コンストラクタ
	 * <p>
	 * 
	 * @throws Exception 例外
	 */
	public LoginAction() throws Exception {
		super();
	}
	
	/**
	 * ログイン処理
	 * <p>
	 * @return 表示ページ
	 */
	@SuppressWarnings("unchecked")
	public String login() {
		
		log.info("ログイン処理を行います");
		log.info("ログインパラメータ："+loginParam);

		getEntityManager().clear();
		
		// エラー時表示画面
		String errret="";
		
		// 起動情報のクリア
		kidoInfo="";
		
		// 自動ログイン判定
		boolean autoFlg = false;

		sescnt = 0L;
        if (AtgUtils.checkMust(loginParam)) {
        	// 自動ログインパラメータ有りの場合、
    		// ログインパラメータを分解し、会社コード・社員コードを取得する
        	autoFlg = autoLogin();
        	
        	// 自動ログイン時の戻り画面
        	errret="autoLoginError";
        }
		
		// 会社マスタから情報を取得
        List zmcList = getEntityManager().createQuery("from EzZMCOMPANY where kc = :kc ")
        .setParameter("kc", kc)
		.getResultList();
		
		if (zmcList.size()==0) {
			facesMessages.add("会社情報が取得できません");
			facesMessages.add("会社コード・ユーザーＩＤ・パスワードを確認して下さい");
			return errret;
		}
		
		EzZMCOMPANY zmcompany = (EzZMCOMPANY)zmcList.get(0);
		
        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		// 業務会社情報を取得
		if (!AtgUtils.checkMust(gkc)) {
			gkc = kc;
		}
        List gmcList = getEntityManager().createQuery("from EzZMCOMPANY where kc = :kc ")
        .setParameter("kc", gkc)
		.getResultList();
		
		if (gmcList.size()==0) {
			facesMessages.add("業務会社情報が取得できません");
			facesMessages.add("会社コード・ユーザーＩＤ・パスワードを確認して下さい");
			return errret;
		}
		
		EzZMCOMPANY gmcompany = (EzZMCOMPANY)gmcList.get(0);
        // △Modify 2010/10/20 Jinno	業務部門変更機能対応

		// 社員マスタから情報を取得
        List ziuserList = null;
        
    	if (autoFlg==true || pass.equals("autoLogin!")) {
    		ziuserList = getEntityManager().createQuery("from EzZIUSER where kc = :kc and sycd = :sycd")
            .setParameter("kc", kc)
            .setParameter("sycd", user)
    		.getResultList();
    	} else {
    		ziuserList = getEntityManager().createQuery("from EzZIUSER where kc = :kc and sycd = :sycd and pass = :pass")
            .setParameter("kc", kc)
            .setParameter("sycd", user)
            .setParameter("pass", pass)
    		.getResultList();
    	}
		
		if (ziuserList.size()==0) {
			facesMessages.add("社員情報が取得できません");
			facesMessages.add("会社コード・ユーザーＩＤ・パスワードを確認して下さい");
			return errret;
		}
		
		EzZIUSER ziuser = (EzZIUSER)ziuserList.get(0);
		
		// 部門マスタから情報を取得
		List zibumstList = getEntityManager().createQuery("from EzZIBUMST where kc = :kc and bucd = :bucd")
        .setParameter("kc", kc)
        .setParameter("bucd", ziuser.getSybu())
		.getResultList();
		
		if (zibumstList.size()==0) {
			facesMessages.add("部門情報が取得できません");
			facesMessages.add("会社コード・ユーザーＩＤ・パスワードを確認して下さい");
			return errret;
		}
		
		EzZIBUMST zibumst = (EzZIBUMST)zibumstList.get(0);

        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		// 部門マスタから情報を取得
		if (!AtgUtils.checkMust(gbu)){
			gbu = ziuser.getSybu();
		}
		List gbumstList = getEntityManager().createQuery("from EzZIBUMST where kc = :kc and bucd = :bucd")
        .setParameter("kc", gkc)
        .setParameter("bucd", gbu)
		.getResultList();
		
		if (gbumstList.size()==0) {
			facesMessages.add("業務部門情報が取得できません");
			facesMessages.add("会社コード・ユーザーＩＤ・パスワードを確認して下さい");
			return errret;
		}
		
		EzZIBUMST gbumst = (EzZIBUMST)gbumstList.get(0);
        // △Modify 2010/10/20 Jinno	業務部門変更機能対応

		// システム情報ＤＢから情報を取得
        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		//List zisysList = getEntityManager().createQuery("from EzZISYS where kc = :kc")
        //.setParameter("kc", kc)
        //.getResultList();
		String wkc = "";
		if (kc.equals(gkc)){
			wkc = kc;
		} else {
			wkc = gkc;
		}
		List zisysList = getEntityManager().createQuery("from EzZISYS where kc = :kc")
        .setParameter("kc", wkc)
        .getResultList();
        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
		
		if (zisysList.size()==0) {
			facesMessages.add("システム情報が取得できません");
			facesMessages.add("会社コード・ユーザーＩＤ・パスワードを確認して下さい");
			return errret;
		}
		
		EzZISYS zisys = (EzZISYS)zisysList.get(0);
		
		// ログインビーンを作成
        LoginBean lbb = new LoginBeanBase();
        
		// 会社コード
        lbb.setKc(kc);
        
		// 会社名
        lbb.setKcnm(zmcompany.getKcmei());
	
		// 従業員コード
        lbb.setSycd(user);
	
		// 従業員名称
        lbb.setSymei(ziuser.getSymei());
	
		// パスワード
        lbb.setPass(pass);
	
		// パスワード有効期限
        lbb.setPasslimit(ziuser.getPasslimit());
    	
		// 営業所コード
        lbb.setSybu(ziuser.getSybu());
	
		// クライアントIP
        lbb.setClip(AtgUtils.getClientIpAddress());
	
		//ＣＰＵ名
        lbb.setClnm(AtgUtils.getCpuName(AtgUtils.getClientIpAddress()));

		//ログイン時間
        lbb.setLoginTime(AtgUtils.getSystemTime(1));
	
		//本部区分
        lbb.setHbkb(zibumst.getHbkb());

		//部門名
        lbb.setBumei(zibumst.getBumei());
    	
		//短縮部門名
        lbb.setTbumei(zibumst.getTbumei());
	
		//ジョブ日付
        lbb.setDate(zisys.getJobdt());
	
        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		// 業務会社コード
        lbb.setGkc(gkc);
        
		// 業務会社名
        lbb.setGkname(gmcompany.getKcmei());
	
		//業務営業所コード
        lbb.setGbu(gbumst.getBucd());
	
		//業務本部区分
        lbb.setGhbkb(gbumst.getHbkb());
    	
		//業務部門名
        lbb.setGbumei(gbumst.getBumei());
    	
		//短縮業務部門名
        lbb.setGtbumei(gbumst.getTbumei());
        
        //部門変更フラグ
        if (kc.equals(gkc) && ziuser.getSybu().equals(gbu)) {
        	lbb.setWsc(false);
        } else {
        	lbb.setWsc(true);
        }
        // △Modify  2010/10/20 Jinno	業務部門変更機能対応

        // ▽Modify 2011/01/18 Jinno	業務間メッセージ対応
    	//業務間メッセージ
    	lbb.setMessage(message);
        // △Modify 2010/01/18 Jinno	業務間メッセージ対応

        //プリントサーバーＩＰ
        lbb.setPrtServerIp(AtgUtils.getIPaddress("atgpt1"));

		//スプールサーバーＩＰ
        lbb.setSplServerIp(AtgUtils.getIPaddress("atgsl1"));
        
		//ＤＢサーバーＩＰ
        lbb.setDbServerIp(AtgUtils.getIPaddress("atgdb1"));

		//本稼動サーバー判断（"1"＝本稼動環境）
//        if (lbb.getDbServerIp().substring(0, 9).equals("10.20.222")) {
//        	lbb.setHonKbn("1");
//        } else {
        	lbb.setHonKbn("0");
//        }

		//自サーバーＩＰ
        lbb.setApServerIp(AtgUtils.getIPaddress("myip"));

		//起動パラメータ
        lbb.setLoginParam(loginParam);
	
		//権限取得
        lbb.setAuthority("");

		//起動情報
        lbb.setKidoInfo(kidoInfo);
        
		//起動情報
        lbb.setProjectName(AtgUtils.getContextPath().replace("/", ""));
        
        //セッションカウント
        lbb.setSescnt(sescnt);
		
        //loginUserコンテキストへ追加
        Contexts.getSessionContext().set("loginUser", lbb);
		
        if (autoFlg == false) {
			log.info("★ログインしました！");
			log.info("//会社コード                   "+lbb.getKc());
			log.info("//従業員コード                 "+lbb.getSycd());
			log.info("//従業員名称                   "+lbb.getSymei());
			log.info("//パスワード                   "+lbb.getPass());
			log.info("//パスワード有効期限           "+lbb.getPasslimit());
			log.info("//営業所コード                 "+lbb.getSybu());
			log.info("//クライアントIP               "+lbb.getClip());
			log.info("//ＣＰＵ名                     "+lbb.getClnm());
			log.info("//ログイン時間                 "+lbb.getLoginTime());
			log.info("//本部区分                     "+lbb.getHbkb());
			log.info("//部門名                       "+lbb.getBumei());
			log.info("//短縮部門名                   "+lbb.getTbumei());
			log.info("//ジョブ日付                   "+lbb.getDate());
			if (lbb.isWsc()){
				log.info("//部門変更フラグ               true");
			} else {
				log.info("//部門変更フラグ               false");
			}
			log.info("//業務会社コード               "+lbb.getGkc());
			log.info("//業務営業所コード             "+lbb.getGbu());
			log.info("//業務本部区分                 "+lbb.getGhbkb());
			log.info("//業務会社名                   "+lbb.getGkname());
			log.info("//業務部門名                   "+lbb.getGbumei());
			log.info("//短縮業務部門名               "+lbb.getGtbumei());
			log.info("//プリントサーバーＩＰ         "+lbb.getPrtServerIp());
			log.info("//スプールサーバーＩＰ         "+lbb.getSplServerIp());
			log.info("//ＤＢサーバーＩＰ             "+lbb.getDbServerIp());
			log.info("//本稼動サーバー判断（1＝本稼）"+lbb.getHonKbn());
			log.info("//自サーバーＩＰ               "+lbb.getApServerIp());
			log.info("//起動パラメータ               "+lbb.getLoginParam());
			log.info("//表示画面ＩＤ                 "+lbb.getGid());
			log.info("//権限取得                     "+lbb.getAuthority());
			log.info("//起動情報                     "+lbb.getKidoInfo());
			log.info("//自プロジェクト名称           "+lbb.getProjectName());
			log.info("//セッションカウント           "+lbb.getSescnt());
    		log.info("//初回ログインです、セッション情報をクリアします");
    		
    		sessionDelete(lbb.getKc(),lbb.getSycd(),lbb.getClip(),0);
    		
			// セッション情報(ZI_SESSION)を作成
			EzZISESSION zises = new EzZISESSION();
			zises.setKc(lbb.getKc());
			zises.setSycd(lbb.getSycd());
			zises.setIp(lbb.getClip());
			zises.setProject(lbb.getProjectName());
			zises.setJobdt(lbb.getDate());
			zises.setRtnmid("");
			zises.setRtnparam("");
			zises.setSessioncnt(0);
			zises.setUpdtime(AtgUtils.getNowTime());
			getEntityManager().persist(zises);
        } else {
			log.info("★自動ログインしました！");
			log.info("//会社コード                   "+lbb.getKc());
			log.info("//従業員コード                 "+lbb.getSycd());
			log.info("//クライアントIP               "+lbb.getClip());
			log.info("//起動パラメータ               "+lbb.getLoginParam());
        }

		// ログイン状態とする
		Contexts.getSessionContext().set("loggedIn", true);
		
        if (autoFlg) {
        	lbb.setGid(gid);
            log.info("業務画面表示処理を行います「"+loginParamMenu+"」");
        }
        
		return "menu";
	}
	
	/**
	 * オートログイン処理
	 * <p>
	 * ログインパラメータを取得し、オートログインできるかを判定<p>
	 * オートログインできる場合、パラメータを分解し起動情報を取得する<p>
	 * @return オートログインできる場合=true
	 */
	private boolean autoLogin() {
		
		// autoLoginできるか？
		boolean autoLogin = false;
		
		// CSV処理用
		CSVTokenizer csvt = null;
		
		// 自動ログインパラメータが存在する場合
		Object obj = loginParam;
		if (obj != null) {
			csvt = new CSVTokenizer(loginParam);
			csvt.setToken(',');
			if (csvt.countTokens() >= 4){
				autoLogin = true;
			}
		}
		// 会社コード,ユーザーＩＤ,起動クラス名,パラメータ・・・・（１０個まで）
		// 起動クラス名を取得する
		String clsnm = "";
		if (autoLogin == true) {
	        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
			// 会社コード
			// kc = csvt.nextToken();
			// 一つ目のCSVトークンを取得
			String tkn = csvt.nextToken();
			// 取得したトークンを‘:’で分割
			// params[0]	ログイン会社コード
			// params[1]	業務会社コード
			// params[2]	業務部門コード
			String params[] = tkn.split(":");
			if (params.length < 3){
				// 会社コード
				kc = params[0];
				// 業務会社コード
				gkc = "";
				// 業務部門コード
				gbu = "";
			} else {
				// 会社コード
				kc = params[0];
				// 業務会社コード
				gkc = params[1];
				// 業務部門コード
				gbu = params[2];
			}
	        // △Modify 2010/10/20 Jinno	業務部門変更機能対応

			// ユーザーＩＤ
			user = csvt.nextToken();
			// セッションカウント
			String ses="";
			try {
				ses = csvt.nextToken();
				sescnt = Long.parseLong(ses);
			} catch (Exception e) {
				log.info("セッションカウントの取得にてエラーが発生しました["+ses+"]");
				sescnt = 0L;
			}
			// 起動情報
			kidoInfo = csvt.nextToken();
			// 起動クラス名
			clsnm = csvt.nextToken();
		}
		
		int exec = 0;
		
		if (autoLogin == true && clsnm.equals("RETURN_MENU")) {
			// メニューに戻る処理
			exec=0;
		}
		if (autoLogin == true && !clsnm.equals("RETURN_MENU")) {
			// 自動ログイン処理
			exec=1;
		}
		if (autoLogin == false) {
			// 手動ログイン処理
			exec=3;
		}
		if (autoLogin == true && clsnm.equals("RETURN_MENU2")) {
			// メニューに戻る処理
			exec=2;
		}
		
		switch (exec) {
		case 0:
			// メニューに戻る処理
//			menuId = csvt.nextToken();
			String gbu = csvt.nextToken();
			Contexts.getSessionContext().set("gbu", gbu);
			getLoginBean().setGbu(gbu);
			
			loginParamMenu = "t";
			for (int i=0; i<10; i++) {
				loginParamParam[i] = "p";
				loginParamValue[i] = "v";
			}
			loginAutoPush = "";
			break;
		case 1:
			// 自動ログイン処理
			loginParamMenu = "#{"+clsnm+"}";
			// パラメータの数だけ初期化
			for (int i=0; i<10; i++) {
				loginParamParam[i] = "p";
				loginParamValue[i] = "v";
			}
			// パラメータの埋め込み
			int cnt = 0;
			while (csvt.hasMoreTokens()) {
				// パラメータ
				String str = csvt.nextToken();
				loginParamParam[cnt] = str;
				if (csvt.hasMoreTokens()) {
					// 値
					str = csvt.nextToken();
					loginParamValue[cnt] = str;
				}
				cnt = cnt + 1;
			}
			loginAutoPush = "1";
			break;
		case 2:
			// 自動ログインパラメータが存在しない場合
			loginParamMenu = "t";
			for (int i=0; i<10; i++) {
				loginParamParam[i] = "p";
				loginParamValue[i] = "v";
			}
			loginAutoPush = "";
			gid = csvt.nextToken();
			break;
		case 3:
			// 会社コード
			kc = csvt.nextToken();
			// ユーザーＩＤ
			user = csvt.nextToken();
			// パスワード
			pass = "autoLogin!";
			// 自動ログインパラメータが存在しない場合
			loginParamMenu = "t";
			for (int i=0; i<10; i++) {
				loginParamParam[i] = "p";
				loginParamValue[i] = "v";
			}
			loginAutoPush = "";

		}
		return autoLogin;
	}

	/**
	 * リンク先の設定を行います.
	 * @return リンク先画面
	 */
	public String linkInit() {
		MenuSub ms = new MenuSub();
		return 	ms.link_exec(getEntityManager(), getLoginBean(), "2", "", "", loginParamMenu, log);
	}

	/**
	 * 次画面リンク情報を設定し、次画面遷移を行います.
	 * @return 次リンク先画面
	 */
	public String nextLink() {
		
		Long sessioncnt = 0L;
		
		// 現在のセッションカウントを取得
		if (getLoginBean().getSescnt() != null) {
			sessioncnt = getLoginBean().getSescnt();
		}
		
		// 現在のセッション以上の情報があれば、クリアする
		sessionDelete(getLoginBean().getKc(), getLoginBean().getSycd(), getLoginBean().getClip(), sessioncnt+1);
		
		boolean ret = false;
		
		if (!getLoginBean().getRtnmid().equals("")) {
			// 次のリンクに遷移する場合
			
			// 現セッション情報の更新
			EzZISESSION ses = (EzZISESSION) getEntityManager().createQuery(
					"from EzZISESSION "
					+"where kc = :kc"
					+"  and sycd = :sycd"
					+"  and ip = :ip"
					+"  and sessioncnt = :sescnt")
	        .setParameter("kc", getLoginBean().getKc())
	        .setParameter("sycd", getLoginBean().getSycd())
	        .setParameter("ip", getLoginBean().getClip())
	        .setParameter("sescnt", getLoginBean().getSescnt())
			.getSingleResult();
			ses.setRtnmid(getLoginBean().getRtnmid());
			ses.setRtnparam(getLoginBean().getRtnparam());
			getEntityManager().merge(ses);
			
			// 次のセッション情報(ZI_SESSION)を作成
			EzZISESSION zises = new EzZISESSION();
			zises.setKc(getLoginBean().getKc());
			zises.setSycd(getLoginBean().getSycd());
			zises.setIp(getLoginBean().getClip());
			zises.setProject(getLoginBean().getProjectName());
			zises.setJobdt(getLoginBean().getDate());
			zises.setMid(getLoginBean().getNextmid());
			zises.setRtnmid("");
			zises.setRtnparam("");
			zises.setSessioncnt(sessioncnt+1);
			zises.setUpdtime(AtgUtils.getNowTime());
			getEntityManager().persist(zises);
			
		} else {
			// 前のリンクに戻る場合
			
			// 現セッション情報(ZI_SESSION)を削除
			EzZISESSION ses = (EzZISESSION) getEntityManager().createQuery(
					"from EzZISESSION "
					+"where kc = :kc"
					+"  and sycd = :sycd"
					+"  and ip = :ip"
					+"  and sessioncnt = :sescnt")
	        .setParameter("kc", getLoginBean().getKc())
	        .setParameter("sycd", getLoginBean().getSycd())
	        .setParameter("ip", getLoginBean().getClip())
	        .setParameter("sescnt", getLoginBean().getSescnt())
			.getSingleResult();
			getEntityManager().remove(ses);
			ret = true;
		}
		
		// コンテキスト、次リンク情報作成
		String nextLink = makeLink(
				getEntityManager(),
				getLoginBean(),
				log,
				getLoginBean().getNextmid(),
//				"fromOutcome,"+getLoginBean().getFacesName()+","+getLoginBean().getNextparam(),
				"fromOutcome,"+"menu"+","+getLoginBean().getNextparam(),
				ret);
		
		log.info("★リンクします："+nextLink);
		
		Contexts.getApplicationContext().set(getLoginBean().getClip(),nextLink);
		
		return "nextLink2";
	}

	/**
	 * メニューに戻る処理
	 * 次画面をセッションカウント0のメニューに設定し、次画面遷移を行います.
	 * @return 次リンク先画面
	 */
	@SuppressWarnings("unchecked")
	public String returnMenu() {
		
		// 現在のセッション以上の情報があれば、クリアする
		sessionDelete(getLoginBean().getKc(), getLoginBean().getSycd(), getLoginBean().getClip(), 1);
		
		// 戻り先情報を取得
		List<EzZISESSION> sesList = getEntityManager().createQuery(
				"from EzZISESSION "
				+" where kc = :kc"
				+"   and sycd = :sycd"
				+"   and ip = :ip"
				+"   and sessioncnt = :sescnt"
				+" order by sessioncnt desc ")
        .setParameter("kc", getLoginBean().getKc())
        .setParameter("sycd", getLoginBean().getSycd())
        .setParameter("ip", getLoginBean().getClip())
        .setParameter("sescnt", 0L)
		.getResultList();
		
		if (sesList.size() > 0) {
			EzZISESSION session = sesList.get(0);
			
			// 戻り先情報をLoginBeanにセットする
			getLoginBean().setNextmid(session.getRtnmid());
			getLoginBean().setNextparam(session.getRtnparam());
			getLoginBean().setRtnmid("");
			getLoginBean().setRtnparam("");
			
		}
		
		// コンテキスト、次リンク情報作成
		String nextLink = makeLink(
				getEntityManager(),
				getLoginBean(),
				log,
				getLoginBean().getNextmid(),
				getLoginBean().getNextparam(),
				true);
		
		log.info("★リンクします："+nextLink);
		
		Contexts.getApplicationContext().set(getLoginBean().getClip(),nextLink);
		
		return "nextLink2";
	}
	
	/**
	 * コマンド遷移処理。
	 * @return 表示ページ
	 */
	@SuppressWarnings("unchecked")
	public String commandJump() {
		
		//コマンド失敗時の戻り先
		String ret = getLoginBean().getFacesName();
		
		//次画面ID
		String gid = "";
		
		//次画面パラメータ
		String param = "";
		
		//コンテキストより、commandを取得
		command = Contexts.getSessionContext().get("command").toString();
		
		//コマンドの分解を行う
		if (LafitUtils.checkMust(command)) {
			//SPACEにて分解
			String cmd[] = command.split(" ");
			//ログに出力
			for (int i=0;i<cmd.length;i++) {
				log.info("command["+i+"]=["+cmd[i]+"]");
				//1つめは画面ID
				if (i==0) {
					gid = cmd[i];
				}
				//2つめは画面パラメータ
				if (i==1) {
					param = cmd[i];
				}
			}
		}
		
		//リンク先が存在するかをチェック
        // ▽Modify 2011/07/14 Jinno	業務部門変更機能対応
		//List itmList = getEntityManager().createQuery ("from EzZMMENUITM where kc = :kc and gid = :gid")
		//.setParameter("kc", getLoginBean().getKc())
		//.setParameter("gid", gid)
		//.getResultList();
		List itmList = getEntityManager().createQuery ("from EzZMMENUITM where kc = :kc and gid = :gid")
		.setParameter("kc", getLoginBean().getGkc())
		.setParameter("gid", gid)
		.getResultList();
        // △Modify 2011/07/14 Jinno	業務部門変更機能対応

		if (itmList.size()>0) {
			//リンク先が存在した！
			
			//セッションカウントを0にする（メニューからの遷移と同じ動きをする）
			getLoginBean().setSescnt(0L);
			
			//メニュー項目マスタからレコードの取得
			EzZMMENUITM itm = (EzZMMENUITM)itmList.get(0);
			
			//▽ Add 2011/05/20 Jinno			業務制限対応
			//業務制限が‘0’以外の場合、制限中のメッセージを表示する
			if (!itm.getStlimit().equals("0")){
				return "limitedjob";
			}
			//△ Add 2011/05/20 Jinno			業務制限対応
			
			// ▽ Start  追加    2011/09/13    jinno
			// ユーザー・端末・部門毎の起動制限処理
			boolean ac = acCheck(	itm.getAcusr(),
					itm.getActm(),
					itm.getAcbu(),
					getLoginBean().getSycd(),
					getLoginBean().getClnm(),
					getLoginBean().getGbu());
			if (!ac){
				//制限が設けられている場合は、制限中のメッセージを表示する
				return "limitedjob";
			}
			// △ End    追加    2011/09/13    jinno
			
			//次表示画面IDのセット
			getLoginBean().setNextmid(gid);
			
			if (LafitUtils.checkMust(param)) {
				param = param.replaceAll(",", "!");
			}
			
			
			if (AtgUtils.checkMust(param)) {
				//パラメータ入力された場合、そのパラメータをセット
				if (LafitUtils.checkMust(itm.getStparam())) {
					getLoginBean().setNextparam(itm.getStparam()+",gparam,"+param);
				} else {
					getLoginBean().setNextparam("gparam,"+param);
				}
			} else {
				//パラメータが入力されなかった場合、メニュー項目マスタよりパラメータを取得
				if (LafitUtils.checkMust(itm.getStparam())) {
					getLoginBean().setNextparam(itm.getStparam());
				} else {
					getLoginBean().setNextparam("");
				}
			}

			//セッション0、つまりメニュー時のセッション情報を取得
			EzZISESSION ses = (EzZISESSION) getEntityManager().createQuery(
					"from EzZISESSION "
					+"where kc = :kc"
					+"  and sycd = :sycd"
					+"  and ip = :ip"
					+"  and sessioncnt = :sescnt")
	        .setParameter("kc", getLoginBean().getKc())
	        .setParameter("sycd", getLoginBean().getSycd())
	        .setParameter("ip", getLoginBean().getClip())
	        .setParameter("sescnt", 0L)
			.getSingleResult();
			
			if (AtgUtils.checkMust(ses.getRtnmid()) && AtgUtils.checkMust(ses.getMid())) {
				//業務画面からのコマンド遷移
				getLoginBean().setRtnmid(ses.getRtnmid());
				getLoginBean().setRtnparam(ses.getRtnparam());
			} else {
				//メニュー画面からのコマンド遷移
				getLoginBean().setRtnmid(getLoginBean().getMid());
				getLoginBean().setRtnparam("");
			}
			
			//現在のセッション以上の情報があれば、クリアする
			sessionDelete(getLoginBean().getKc(), getLoginBean().getSycd(), getLoginBean().getClip(), 1);
			
			//次画面処理を行う
			ret = "nextLink";
		}
		
		return ret;
	}
	
	/**
	 * メニューリンク作成
	 * @param em エンティティーマネージャ
	 * @param lb ログインビーン
	 * @param log ログマネージャ
	 * @param mid メニューＩＤ
	 * @param getparam パラメータ
	 * @return 他プロジェクト用のリンク文字列
	 */
	@SuppressWarnings("unchecked")
	public String makeLink(EntityManager em, LoginBean lb, LogManager log, String mid, String param, boolean ret) {
		log.info("メニューリンク作成");
		log.info("メニューＩＤ："+mid);
		
		// メニュー項目マスタから読み込み
		/*
		List menuitmlist = em.createQuery ("from EzZMMENUITM where kc = :kc and gid = :gid")
		.setParameter("kc", lb.getKc())
		.setParameter("gid", mid)
		.getResultList();
		*/
		List menuitmlist = em.createQuery ("from EzZMMENUITM where kc = :kc and gid = :gid")
		.setParameter("kc", lb.getGkc())
		.setParameter("gid", mid)
		.getResultList();
		
		// メニュー項目マスタは存在するか？
		if ( menuitmlist.size()==0 ) {
			return null;
		}
		
		// メニュー項目マスタを取得
		EzZMMENUITM menuitm = (EzZMMENUITM) menuitmlist.get(0);
		
		// 他のプロジェクトへのリンクを組み立てる
		// ex. http://localhost:8080/atg_project1/Common/autoLogin.seam?85,97010,CHILD,project1.init
		//     http://10.30.11.184:8080/tisa_framework/Common/autoLogin.seam?85,97010,2,CHILD,RETURN_MENU2,MNT00
		// ex. http://localhost:8080/atg_project1/Common/autoLogin.seam?85:01:001,97010,CHILD,project1.init
		//     http://10.30.11.184:8080/tisa_framework/Common/autoLogin.seam?85:01:001,97010,2,CHILD,RETURN_MENU2,MNT00
		String link = "";
		link += "http://";
		if (AtgUtils.checkMust(menuitm.getPrjsvip())) {
			link += menuitm.getPrjsvip();
		// ▽ Add 2011/04/24 by Jinno	プロジェクト遷移対応
		} else if (AtgUtils.checkMust(menuitm.getProject())) {
			link += AtgUtils.getIPaddress(menuitm.getProject());
		// △ Add 2011/04/24 by Jinno	プロジェクト遷移対応
		} else {
			link += AtgUtils.getIPaddress(lb.getProjectName());
		}
		link += ":8080/";
		link += menuitm.getProject();
		link += "/Common/autoLogin.seam?";
        // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
		//link += lb.getKc()+",";
		if (AtgUtils.checkMust(lb.getGkc()) && AtgUtils.checkMust(lb.getGbu())){
			link += lb.getKc()+":";
			link += lb.getGkc()+":";
			link += lb.getGbu()+",";
		} else {
			link += lb.getKc()+",";
		}
        // △Modify 2010/10/20 Jinno	業務部門変更機能対応
		link += lb.getSycd()+",";
		if (ret == true) {
			link += (lb.getSescnt()-1)+",";
		} else {
			link += (lb.getSescnt()+1)+",";
		}
		if (lb.getSescnt() < 2 && ret == true) {
			link += ",";
		} else {
			link += "CHILD,";
		}
		
		// メニューへ戻るリンクかの判断
		boolean menulink=false;
		if (LafitUtils.checkMust(menuitm.getStparam())) {
			if (menuitm.getStparam().equals("menu")) {
				menulink=true;
			}
		}
		
		if (menulink==false) {
			// 業務画面へ戻る
			String sta = menuitm.getStaddress();
			if (AtgUtils.checkMust(sta)) {
				sta = sta.replace("#{", "");
				sta = sta.replace("}", "");
			} else {
				sta = "";
			}
			link += sta+",";
			link += param;
		} else {
			// メニューへ戻る
			link += "RETURN_MENU2,";
			link += menuitm.getGid();
		}
		
		return link;
		//return "noservice.jsp";
	}
	
	/**
	 * セッション情報のクリア
	 */
	@SuppressWarnings("unchecked")
	private void sessionDelete(String kc, String sycd, String clip,long sessioncnt) {
		// 現在のセッション以上の情報があれば、クリアする
		List<EzZISESSION> sesList = getEntityManager().createQuery(
				"from EzZISESSION "
				+"where kc = :kc"
				+"  and sycd = :sycd"
				+"  and ip = :ip"
				+"  and sessioncnt >= :sescnt")
        .setParameter("kc", kc)
        .setParameter("sycd", sycd)
        .setParameter("ip", clip)
        .setParameter("sescnt", sessioncnt)
		.getResultList();
		
		// クリア
		for (EzZISESSION colData :sesList) {
			getEntityManager().remove(colData);
		}
	}
	
	/**
	 * リンクを取得します。
	 * @return リンク
	 */
	public String getLink() {
		// セッションクローズ
		Session.instance().invalidate();
		
		return (String)Contexts.getApplicationContext().get(AtgUtils.getClientIpAddress());
	}
	
	/**
	 * logout処理
	 */
	public String logout() throws Exception {
		
		// セッション情報をすべてクリアする
		sessionDelete(getLoginBean().getKc(), getLoginBean().getSycd(), getLoginBean().getClip(), 0);
		
		// Seamセッションの終了
		Session.instance().invalidate();
        
		// ページCLOSE画面へ遷移
		return "close";
	}
	
	// ▽ Start  追加    2011/09/13    jinno
	// ユーザー・端末・部門毎の起動制限処理
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
	// △ End    追加    2011/09/13    jinno
    
	/**
	 * 開放
	 */
	@Destroy @Remove
	public void destroy() {
		log.info("DestroyCs");
	}
	
	/**
	 * パラメータを取得します。
	 * @return パラメータ
	 */
	public String getLoginParam() {
		return loginParam;
	}

	/**
	 * パラメータを設定します。
	 * @param loginParam パラメータ
	 */
	public void setLoginParam(String loginParam) {
		this.loginParam = loginParam;
	}

	/**
	 * メッセージを取得します。
	 * @return メッセージ
	 */
	public FacesMessages getFacesMessages() {
	    return facesMessages;
	}

	/**
	 * メッセージを設定します。
	 * @param facesMessages メッセージ
	 */
	public void setFacesMessages(FacesMessages facesMessages) {
	    this.facesMessages = facesMessages;
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

    // ▽Modify 2010/10/20 Jinno	業務部門変更機能対応
	/**
	 * 業務会社コードを取得します。
	 * @return 業務会社コード
	 */
	public String getGkc() {
	    return gkc;
	}

	/**
	 * 業務会社コードを設定します。
	 * @param gkc 業務会社コード
	 */
	public void setGkc(String gkc) {
	    this.gkc = gkc;
	}

	/**
	 * 業務部門コードを取得します。
	 * @return 業務部門コード
	 */
	public String getGbu() {
	    return gbu;
	}

	/**
	 * 業務部門コードを設定します。
	 * @param gbu 業務部門コード
	 */
	public void setGbu(String gbu) {
	    this.gbu = gbu;
	}
    // △Modify 2010/10/20 Jinno	業務部門変更機能対応

	/**
	 * ユーザＩＤを取得します。
	 * @return ユーザＩＤ
	 */
	public String getUser() {
		return user;
	}

	/**
	 * ユーザＩＤを設定します。
	 * @param user ユーザＩＤ
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * パスワードを取得します。
	 * @return パスワード
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * パスワードを設定します。
	 * @param pass パスワード
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * 起動情報を取得します。
	 * @return 起動情報
	 */
	public String getKidoInfo() {
	    return kidoInfo;
	}

	/**
	 * 起動情報を設定します。
	 * @param kidoInfo 起動情報
	 */
	public void setKidoInfo(String kidoInfo) {
	    this.kidoInfo = kidoInfo;
	}

	/**
	 * 自動押下用フラグを取得します。
	 * @return 自動押下用フラグ
	 */
	public String getLoginAutoPush() {
	    return loginAutoPush;
	}

	/**
	 * 自動押下用フラグを設定します。
	 * @param loginAutoPush 自動押下用フラグ
	 */
	public void setLoginAutoPush(String loginAutoPush) {
	    this.loginAutoPush = loginAutoPush;
	}

	/**
	 * 初期起動メソッド名を取得します。
	 * @return 初期起動メソッド名
	 */
	public String getLoginParamMenu() {
	    return loginParamMenu;
	}

	/**
	 * 初期起動メソッド名を設定します。
	 * @param loginParamMenu 初期起動メソッド名
	 */
	public void setLoginParamMenu(String loginParamMenu) {
	    this.loginParamMenu = loginParamMenu;
	}

	/**
	 * 初期起動パラメタ名を取得します。
	 * @return 初期起動パラメタ名
	 */
	public String[] getLoginParamParam() {
	    return loginParamParam;
	}

	/**
	 * 初期起動パラメタ名を設定します。
	 * @param loginParamParam 初期起動パラメタ名
	 */
	public void setLoginParamParam(String[] loginParamParam) {
	    this.loginParamParam = loginParamParam;
	}

	/**
	 * 初期起動パラメタ値を取得します。
	 * @return 初期起動パラメタ値
	 */
	public String[] getLoginParamValue() {
	    return loginParamValue;
	}

	/**
	 * 初期起動パラメタ値を設定します。
	 * @param loginParamValue 初期起動パラメタ値
	 */
	public void setLoginParamValue(String[] loginParamValue) {
	    this.loginParamValue = loginParamValue;
	}
	
	/**
	 * ヘッダコマンドを取得します。
	 * @return ヘッダコマンド
	 */
	public String getCommand() {
	    return command;
	}

	/**
	 * ヘッダコマンドを設定します。
	 * @param command ヘッダコマンド
	 */
	public void setCommand(String command) {
	    this.command = command;
	}

    // ▽Modify 2011/01/18 Jinno	画面間メッセージ対応
	/**
	 * 画面間メッセージを取得します。
	 * @return 画面間メッセージ
	 */
	public String getMessage() {
	    return message;
	}

	/**
	 * 画面間メッセージを設定します。
	 * @param message 画面間メッセージ
	 */
	public void setMessage(String message) {
	    this.message = message;
	}
    // △Modify 2010/01/18 Jinno	画面間メッセージ対応

}
