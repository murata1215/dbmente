<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="../../tld/lafittag.tld" prefix="lafit"%>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<!-- ########################### 定義エリア ここから ########################### -->
<html>
<meta http-equiv="pragma" content="non-cache">
<meta http-equiv="cache-control" content="non-cache">
<meta http-equiv="expires" content="0">
<head>
<title>テーブルメンテナンス</title>
<link rel="stylesheet" href="../../css/frame.css" type="text/css">
<!--[if IE 6]>
<link rel="stylesheet" href="../../css/ie6.css" type="text/css">
<![endif]-->
<link rel="stylesheet" href="../../css/form.css" type="text/css">
<link rel="stylesheet" href="../../css/textstyle_m.css" type="text/css" title="middle">
<link rel="alternate stylesheet" href="../../css/textstyle_l.css" type="text/css" title="large">
<link rel="alternate stylesheet" href="../../css/textstyle_s.css" type="text/css" title="small">
<script type="text/javascript" src="../../js/jquery.js"></script>
<script type="text/javascript" src="../../js/jquery.hotkeys.js"></script>
<script type="text/javascript" src="../../js/include.js"></script>
<script type="text/javascript" src="../../js/fontsize.js"></script>
<script type="text/javascript" src="../../js/function.js"></script>
<!--  リスト部の項目幅を設定 -->
<style TYPE="text/css">
<!--
	.listTable1 {width: 28px;}
	.listTable2 {width: 38px;}
	.listTable3 {width: 141px;}
	.listTable4 {width: 171px;}
	.listTable5 {width: 51px;}
	.listTable6 {width: 61px;}
	.listTable7 {width: 61px;}
	.listTable8 {width: 61px;}
	.listTable9 {width: 48px;}
	.listTable10 {width: 98px;}
	.listTable11 {width: 140px;}
-->
</style>	
</head>
<body onLoad="focusError();">
<jsp:include page="../../Common/senddata.jsp" flush="true" />
<f:view>
<div id="ALL_DIV" style="display:block">
<div id="wrap">
<!-- ヘッダー -->
<div id="header"><jsp:include page="../../Common/atg_header.jsp" flush="true"><jsp:param name="img_path" value="../../img" /></jsp:include></div>
<div id="content-wrap">
<div id="content">
<!-- メイン　ここから -->
<h:form id="frm1">
<input type="hidden" value="Z102TblMente" name="fromOutcome" />
<!-- ########################### 定義エリア ここまで ########################### -->
	

<!-- ########################### 業務メニュー ここから ########################### -->
<!-- 業務メニュー Start -->
<div class="gmenuHeader">
	<table class="layoutTable"><tr>
		<td class="firstCaption">処理選択</td>
		<td class="value">
		<h:selectOneRadio id="prsKbnRdo" value="#{Z102TblMente.prsKbn}" disabled="#{Z102TblMente.ableHeadItem == 'false'}" >
			<f:selectItem id="prsKbn1" itemValue="1" itemLabel="1:新規"/>
			<f:selectItem id="prsKbn2" itemValue="2" itemLabel="2:修正"/>
			<f:selectItem id="prsKbn3" itemValue="3" itemLabel="3:削除"/>
			<f:selectItem id="prsKbn4" itemValue="4" itemLabel="4:照会"/>
		</h:selectOneRadio>
		</td>
	</tr><tr>
		<td class="firstCaption">テーブルID</td>
		<td>
		<h:inputText id="tblnm" value="#{Z102TblMente.tblnm}" 
			styleClass="ro#{Z102TblMente.ableHeadItem == 'false'}"
			style="width:125px; ime-mode:disabled;" maxlength="15" 
			onblur="focusCheck();checkInputElementFormat(this,['hankaku'],'');"
			onkeyup="toUpper(this);focusNext(this.form, this, true);"
			onfocus="jsReadonly(this,'#{Z102TblMente.ableHeadItem == 'false'}');"
			accesskey="#{Z102TblMente.screenErrors['tblnm']}" />
		<lafit:commandDialog value="入力支援" styleClass="" rendered="#{Z102TblMente.ableHeadItem == 'true'}" image="../../img/select_win_show.gif">
			<f:param name="FIND_PATTERN" value="SQLZ003"/>
			<f:param name="RETURN_ITEM"  value=",tblnm,,tbljnm"/>
			<f:param name="END_SCRIPT"	 value="" />
		</lafit:commandDialog>
		<h:outputText id="tbljnmHead" value="　#{Z102TblMente.tbljnm}" />
		</td>
	</tr></table>
	<%-- 次へボタン自動押下 --%>
	<lafit:hiddenNext value="#{Z102TblMente.showMenu}" id="auto" autoButtonId="nextButton"/>
</div>
<!-- ########################### 業務メニュー ここまで ########################### -->


<!-- ########################### ボディーエリア ここから ########################### -->
<lafit:outputSpan id="bodyDisp1" start="start" lafitrendered="#{Z102TblMente.nowMode != '8'}" />
<div id="BODY_DIV" class="body">

	<!-- ボディー情報 Start -->
	<div class="blockBody" id="searchResultBlockBody">
		
		<!-- 処理区分1(新規)のみ表示 -->
		<lafit:outputSpan id="bodyDisp1-2Start" start="start" lafitrendered="#{Z102TblMente.nowBusinessKbn == '1' || Z102TblMente.nowBusinessKbn == '2'}" />
		<table class="layoutTable"><tr>
			<td class="firstCaption">テーブル名称</td>
			<td>
			<h:inputText id="tbljnm" value="#{Z102TblMente.tbljnm}" 
				styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
				style="width:225px; ime-mode:active;" maxlength="30" 
				onblur="focusCheck();checkInputElementFormat(this,['zenkaku'],'');"
				onkeyup="focusNext(this.form, this, true);"
				onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
				accesskey="#{Z102TblMente.screenErrors['tbljnm']}" />
			</td>
		</tr></table>
		<lafit:outputSpan id="bodyDisp1-2End" />
		
		<!-- 処理区分1(新規) 2(修正) かつ、ボディー編集状態のみ表示 -->
		<lafit:outputSpan id="bodyDisp1-3Start" start="start" lafitrendered="#{(Z102TblMente.nowBusinessKbn == '1' || Z102TblMente.nowBusinessKbn == '2') && Z102TblMente.ableBodyItem == 'true'}" />
		<table class="layoutTable"><tr>
			<td>ＣＳＶを取り込む場合、右の追加ボタンにてファイルを選択。取込後、F1更新を押下　</td>
			<td>
				<rich:fileUpload fileUploadListener="#{Z102TblMente.listener}"
					id="upload"
					acceptedTypes="csv, txt"
					autoclear="true"
					addControlLabel="追加"
					stopControlLabel="停止"
					listHeight="0"
					listWidth="140px"
					onupload=""
					onuploadcomplete="fnDisabled()"
					immediateUpload="true" >
				</rich:fileUpload>
			</td>
		</tr></table>
		<lafit:outputSpan id="bodyDisp1-3End" />
		
		<!-- ページ見出し -->
		<lafit:outputSpan id="pageViewStart" start="start" lafitrendered="#{Z102TblMenteList.rowCount > 0}" />
		<div class="pageHeader" id="pageHeader" style="width:951px;">
			<h:outputText value="データの最初" rendered="#{Z102TblMente.pageCnt == 0 && Z102TblMente.hasMore == 'true'}" />
			<h:outputText value="データの途中" rendered="#{Z102TblMente.pageCnt != 0 && Z102TblMente.hasMore == 'true'}" />
			<h:outputText value="データの終わり" rendered="#{Z102TblMente.hasMore == 'false'}" />
		</div>
		<lafit:outputSpan id="pageViewEnd" />
			
		<!-- ヘッダ部 Start -->
		<div class="tableHeader" id="searchResultHeader" style="width:951px;">
			<table class="listHeader"><tr>
				<td class="listTable1" style="text-align:center;">選</td>
				<td class="listTable2" style="text-align:center;">NO</td>
				<td class="listTable3" style="text-align:center;">項目ID</td>
				<td class="listTable4" style="text-align:center;">項目名</td>
				<td class="listTable5" style="text-align:center;">型</td>
				<td class="listTable6" style="text-align:center;">長さ1</td>
				<td class="listTable7" style="text-align:center;">長さ2</td>
				<td class="listTable8" style="text-align:center;">必須</td>
				<td class="listTable9" style="text-align:center;">PK</td>
				<td class="listTable10" style="text-align:center;">ﾃﾞﾌｫﾙﾄ</td>
				<td class="listTable11" style="text-align:center;">備考</td>
			</tr></table>
		</div>
		<!-- ヘッダ部 End -->
		        		
	 	<!-- リスト部 Start -->
		<div class="tableData" id="resultListArea" style="width:967px; height:440px; overflow:scroll; top:-15px;"
							onScroll="document.all.searchResultHeader.scrollLeft=this.scrollLeft;">
			<h:dataTable id="list" value="#{Z102TblMenteList}" var="list" 
					 rendered="#{Z102TblMenteList.rowCount > 0}" 
					 border="0" 
					 styleClass="listView" 
					 rowClasses="tblOddRow,tblEvenRow"
					 columnClasses="listTable1 base,listTable2 base,listTable3 base,
					                listTable4 base,listTable5 base,listTable6 base,
					                listTable7 base,listTable8 base,listTable9 base,
					                listTable10 base,listTable11 base">
		
			<h:column>
				<% /* 選 */ %>
				<f:verbatim>
					<input type="radio" style="width:20px;" name="selradio"/>
				</f:verbatim>
			</h:column>
			
			<h:column>
			<h:panelGrid columns="1">
				<% /* 項目NO */ %>
				<h:inputText id="tblno" value="#{list.tblno}" style="width:28px" disabled="true" />
			</h:panelGrid>
			</h:column>
			
			<h:column>
			<h:panelGrid columns="1">
				<% /* 項目ID */ %>
				<h:inputText id="bnm" value="#{list.bnm}" 
					styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
					style="font-family:'ＭＳ ゴシック'; width:125px; ime-mode:disabled;" maxlength="11" 
					onblur="focusCheck();checkInputElementFormat(this,['hankaku'],'');"
					onkeyup="toUpper(this);focusNext(this.form, this, true);"
					onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
					accesskey="#{Z102TblMente.screenErrors['bnm']}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 項目名 */ %>
				<h:inputText id="rnm" value="#{list.rnm}" 
					styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
					style="width:158px; ime-mode:active;" maxlength="25" 
					onblur="focusCheck();checkInputElementFormat(this,['zenkaku'],'');"
					onkeyup="focusNext(this.form, this, true);"
					onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
					accesskey="#{Z102TblMente.screenErrors['rnm']}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 型 */ %>
              	<h:selectOneMenu id="kata1" value="#{list.kata}" rendered="#{Z102TblMente.ableBodyItem == 'true'}" >
					<f:selectItem itemValue=""  itemLabel="    "/>
					<f:selectItem itemValue="9" itemLabel="9"/>
					<f:selectItem itemValue="X" itemLabel="X"/>
					<f:selectItem itemValue="V" itemLabel="V"/>
					<!-- f:selectItem itemValue="N" itemLabel="N"/> -->
					<f:selectItem itemValue="T" itemLabel="T"/>
				</h:selectOneMenu>
              	<h:outputText id="kata2" value="#{list.kata}" rendered="#{Z102TblMente.ableBodyItem == 'false'}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 長さ１ */ %>
				<h:inputText id="lng1" value="#{list.lng1}" 
					styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
					style="width:28px; ime-mode:disabled;" maxlength="3" 
					onblur="focusCheck();checkInputElementFormat(this,['hnumber'],'');"
					onkeyup="focusNext(this.form, this, true);"
					onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
					accesskey="#{Z102TblMente.screenErrors['lng1']}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 長さ２ */ %>
				<h:inputText id="lng2" value="#{list.lng2}" 
					styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
					style="width:28px; ime-mode:disabled;" maxlength="3" 
					onblur="focusCheck();checkInputElementFormat(this,['hnumber'],'');"
					onkeyup="focusNext(this.form, this, true);"
					onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
					accesskey="#{Z102TblMente.screenErrors['lng2']}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 必須マーク */ %>
              	<h:selectOneMenu id="hsu" value="#{list.hsu}" rendered="#{Z102TblMente.ableBodyItem == 'true'}" >
					<f:selectItem itemValue=""  itemLabel="    "/>
					<f:selectItem itemValue="1" itemLabel="必須"/>
				</h:selectOneMenu>
              	<h:outputText id="hsu2" value="必須" rendered="#{Z102TblMente.ableBodyItem == 'false' && list.hsu == '1'}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* プライマリキー */ %>
              	<h:selectOneMenu id="tkey1" value="#{list.tkey1}" rendered="#{Z102TblMente.ableBodyItem == 'true'}" >
					<f:selectItem itemValue=""  itemLabel="　"/>
					<f:selectItem itemValue="U" itemLabel="有"/>
				</h:selectOneMenu>
              	<h:outputText id="tkey1_2" value="有" rendered="#{Z102TblMente.ableBodyItem == 'false' && list.tkey1 == 'U'}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* デフォルト値 */ %>
				<h:inputText id="dflt" value="#{list.dflt}" 
					styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
					style="width:88px; ime-mode:disabled;" maxlength="10" 
					onblur="focusCheck();checkInputElementFormat(this,['hankaku'],'');"
					onkeyup="focusNext(this.form, this, true);"
					onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
					accesskey="#{Z102TblMente.screenErrors['dflt']}" />
			</h:panelGrid>
			</h:column>
	
			<h:column>
			<h:panelGrid columns="1">
				<% /* 備考 */ %>
				<h:inputText id="biko" value="#{list.biko}" 
					styleClass="ro#{Z102TblMente.ableBodyItem == 'false'}"
					style="width:128px; ime-mode:active;" maxlength="1000" 
					onblur="focusCheck();checkInputElementFormat(this,[''],'');"
					onkeyup="focusNext(this.form, this, true);"
					onfocus="jsReadonly(this,'#{Z102TblMente.ableBodyItem == 'false'}');"
					accesskey="#{Z102TblMente.screenErrors['biko']}" />
			</h:panelGrid>
			</h:column>
	
			</h:dataTable>
	
		</div>
	 	<!-- リスト部 END -->
	</div>
	<!-- ボディー情報 End -->

</div>
<lafit:outputSpan id="bodyDisp2" />
<!-- ########################### ボディーエリア ここまで ########################### -->
	
	
	<!-- ########################### ボタンエリア ここから ########################### -->
	<div style="display:none;">
		<!-- /*各処理区分に応じた次へボタン押下時のコマンド用*/ -->
		<%-- 新規 --%>
		<h:commandLink id="prsKbnId1" value="new" action="#{Z102TblMente.createItem}">
			<f:param name="viewMode" value="M030000001"/>
			<f:param name="businessKbn" value="1"/>
		</h:commandLink>	
		<%-- 修正 --%>
		<h:commandLink id="prsKbnId2" value="new" action="#{Z102TblMente.editItem}">
			<f:param name="viewMode" value="M010000001"/>
			<f:param name="businessKbn" value="2"/>
		</h:commandLink>	
		<%-- 削除 --%>
		<h:commandLink id="prsKbnId3" value="new" action="#{Z102TblMente.selectItem}">
			<f:param name="viewMode" value="M020000001"/>
			<f:param name="businessKbn" value="3"/>
		</h:commandLink>	
		<%-- 照会 --%>
		<h:commandLink id="prsKbnId4" value="new" action="#{Z102TblMente.selectItem}">
			<f:param name="viewMode" value="M000000001"/>
			<f:param name="businessKbn" value="4"/>
		</h:commandLink>
		<%-- チェックボタン --%>
		<h:commandButton value="チェック" id="checkButton" styleClass="mainButton" style="display:none"
			rendered="#{Z102TblMente.ableCheck == 'true'}" action="#{Z102TblMente.check}"/>
		<%-- 確定ボタン --%>
		<h:commandButton value="確定" id="torokuButton" styleClass="mainButton" style="display:none"
			rendered="#{Z102TblMente.ableCommit == 'true'}" action="#{Z102TblMente.update}" />
		<%-- メニューに戻るボタン --%>
		<h:commandButton value="戻る" id="returnButton" styleClass="mainButton" style="display:none"
			rendered="#{Z102TblMente.ablePrev == 'false'}" action="#{Z102TblMente.cancel}" />
		<%-- 戻るボタン --%>
		<h:commandButton value="戻る" id="prevButton" styleClass="mainButton" style="display:none"
			rendered="#{Z102TblMente.ablePrev == 'true'}" action="#{Z102TblMente.prev}" />
		<%-- 再検索ボタン --%>
		<h:commandButton value="検索" id="findButton" styleClass="mainButton" style="display:none"
			action="#{Z102TblMente.reFind}" />
		<%-- 印刷ボタン --%>
		<h:commandButton value="印刷" id="printButton" styleClass="mainButton" style="display:none"
			action="#{Z102TblMente.print}" />
		<h:commandButton value="前頁" id="movePrev" styleClass="button" type="button"
			action="#{Z102TblMente.findPrevP}" />
		<h:commandButton value="次頁" id="moveNext" styleClass="button" type="button"
			action="#{Z102TblMente.findNextP}" />
		<h:commandButton value="最後" id="moveLastPage" styleClass="button" type="button"
			action="#{Z102TblMente.command4}" />
		<h:commandButton value="挿入" id="insertButton" styleClass="button" type="button"
			action="#{Z102TblMente.command1}" />
		<h:commandButton value="削除" id="deleteButton" styleClass="button" type="button"
			action="#{Z102TblMente.command2}" />
		<h:commandButton value="最終ページ作成" id="makeLastPage" styleClass="button" type="button"
			action="#{Z102TblMente.command3}" />
		<input type="hidden" value="AZ9990" name="printId" />
		<h:inputHidden id="confirmation" value="#{Z102TblMente.confirmation}" />
		<h:inputText id="columNo" value="#{Z102TblMente.clmno}" style="display:none;" /><br/>
	</div>
	<!-- ########################### ボタンエリア ここまで ########################### -->

</h:form>
<!-- メイン　ここまで -->
</div><!-- end of content -->
</div><!-- end of content-wrap -->

	<!-- ########################### フッタ ここから ########################### -->
    <div id="footer">
	  	<!-- メッセージエリア ここから -->
		<div id="sysmsg">
			<div id="MESSAGE_DIV" class="messageDiv" >
				<h:dataTable value="#{Z102TblMente.messages}" var="ses" border="0" styleClass="tblDetail" rowClasses="tblOddRow, tblEvenRow">
					<h:column><h:outputText value="#{ses.msg}" style="color:red;" /></h:column>
					<h:column><h:outputText value="#{ses.msgAction}" style="color:red;" /></h:column>
					<h:column><h:outputText value="#{ses.errorValue}" style="color:red;" /></h:column>
				</h:dataTable>
			</div>
        </div><!-- end of sysmsg -->
	  	<!-- メッセージエリア ここまで -->
		<!-- Fnキー設定　ここから -->
        <jsp:include page="../../Common/function_buttons.jsp" flush="true">
        	<jsp:param name="textf1" value="更新" /><jsp:param name="statf1" value="enable" />
        	<jsp:param name="textf2" value="修正" /><jsp:param name="statf2" value="enable" />
        	<jsp:param name="textf3" value="　" /><jsp:param name="statf3" value="enable" />
        	<jsp:param name="textf4" value="次ブロック" /><jsp:param name="statf4" value="enable" />
        	<jsp:param name="textf5" value="確定" /><jsp:param name="statf5" value="enable" />
        	<jsp:param name="textf6" value="前頁" /><jsp:param name="statf6" value="enable" />
        	<jsp:param name="textf7" value="次頁" /><jsp:param name="statf7" value="enable" />
        	<jsp:param name="textf8" value="戻る" /><jsp:param name="statf8" value="enable" />
        	<jsp:param name="textf9" value="最後" /><jsp:param name="statf9" value="enable" />
        	<jsp:param name="textf10" value="削除" /><jsp:param name="statf10" value="enable" />
        	<jsp:param name="textf11" value="挿入" /><jsp:param name="statf11" value="enable" />
        	<jsp:param name="textf12" value="印刷" /><jsp:param name="statf12" value="enable" />
        </jsp:include>
		<!-- Fnキー設定　ここまで -->
		<h:inputHidden id="VIEW_KBN" value="#{Z102TblMente.nowMode}" /><br/>
		<h:inputHidden id="VIEW_STATUS" value="#{Z102TblMente.nowModeStatus}"/><br/>
		<h:inputHidden id="BUSINESS_KBN" value="#{Z102TblMente.nowBusinessKbn}"/><br/>
		<h:inputHidden id="ABLE_HEAD_ITEM" value="#{Z102TblMente.ableHeadItem}"/><br/>
		<h:inputHidden id="ABLE_BODY_ITEM" value="#{Z102TblMente.ableBodyItem}"/><br/>
		<h:inputHidden id="ABLE_CHECK" value="#{Z102TblMente.ableCheck}"/><br/>
		<h:inputHidden id="ABLE_PREV" value="#{Z102TblMente.ablePrev}"/><br/>
		<h:inputHidden id="ABLE_COMMIT" value="#{Z102TblMente.ableCommit}"/><br/>
		<h:inputHidden id="ABLE_AFTER_UPDATE" value="#{Z102TblMente.ableAfterUpdate}"/><br/>
		<h:inputHidden id="NOW_MODE" value="#{Z102TblMente.nowMode}"/><br/>
		<h:inputHidden id="ROW_COUNT" value="#{Z102TblMenteList.rowCount}"/><br/>
		<h:inputHidden id="PAGE_COUNT" value="#{Z102TblMente.pageCnt}"/><br/>
		<h:inputHidden id="PAGE_HASMORE" value="#{Z102TblMente.hasMore}"/><br/>
    </div><!-- end of footer -->
	<!-- ########################### フッタ ここまで ########################### -->

<!-- ########################### JavaSctipt設定 ここから ########################### -->
<script type="text/javascript"><!--
	var fid = document.forms[1].id;
	var view_kbn = j$("#VIEW_KBN").val();
	var view_status = j$("#VIEW_STATUS").val();
	var business_kbn = j$("#BUSINESS_KBN").val();
	var able_head_item = j$("#ABLE_HEAD_ITEM").val();
	var able_body_item = j$("#ABLE_BODY_ITEM").val();
	var able_check = j$("#ABLE_CHECK").val();
	var able_prev = j$("#ABLE_PREV").val();
	var able_commit = j$("#ABLE_COMMIT").val();
	var able_after_update = j$("#ABLE_AFTER_UPDATE").val();
	var now_mode = j$("#NOW_MODE").val();
	var row_count = document.getElementById("ROW_COUNT").value;
	var page_count = document.getElementById("PAGE_COUNT").value;
	var hasMore = document.getElementById("PAGE_HASMORE").value;

	//******************************************************************
	// 次へボタンクリック時の処理
	//******************************************************************
	function nextBtnClick() {
		if (document.forms[1].elements[fid +":prsKbnRdo"][0].checked){
			//新規
			j$("#" + fid + "\\:prsKbnId1").trigger("click");
		}
		if(document.forms[1].elements[fid +":prsKbnRdo"][1].checked){
			//修正
			j$("#" + fid + "\\:prsKbnId2").trigger("click");
		}
		if(document.forms[1].elements[fid +":prsKbnRdo"][2].checked){
			//削除
			j$("#" + fid + "\\:prsKbnId3").trigger("click");
		}
		if(document.forms[1].elements[fid +":prsKbnRdo"][3].checked){
			//照会
			j$("#" + fid + "\\:prsKbnId4").trigger("click");
		}
	}

	//******************************************************************
	// 初期化処理
	//******************************************************************
	function init() {
	}

	//******************************************************************
	// 明細再表示処理
	//******************************************************************
	function flushMeisai() {
		j$("#" + fid + "\\:findButton").trigger("click")
	}
	
	//******************************************************************
	// 取り込み時、ファンクション操作
	//******************************************************************
	function fnDisabled() {
		toggleFn("fn2", "disabled");
		toggleFn("fn3", "disabled");
		toggleFn("fn4", "disabled");
		toggleFn("fn5", "disabled");
		toggleFn("fn6", "disabled");
		toggleFn("fn7", "disabled");
		toggleFn("fn8", "disabled");
		toggleFn("fn9", "disabled");
		toggleFn("fn10", "disabled");
		toggleFn("fn11", "disabled");
		toggleFn("fn12", "disabled");
	}

	//******************************************************************
	// 選択カラムＮＯのセット
	//******************************************************************
	function setColumNo() {
		// 明細の中でラジオボタンがチェックされている明細Noを取得してます
		var rtn = getMeisaiNo(document.forms[1].elements["selradio"], fid, false);
		if (rtn=="" || rtn==-1) {
			alert("明細が選択されていません");
			return false;
		} else {
			// 選択されている明細Noが存在する場合、セットします
			document.getElementById(fid + ":columNo").value=rtn;
			return true;
		}
	}

	// ファンクション有効／無効設定
	// F1:
	// 使用しない
//	toggleFn("fn1", "disabled");
	// F2:修正
	// 照会モードの場合、修正画面への遷移として使用
	if (business_kbn!='4' || view_kbn!='1') {
		toggleFn("fn2", "disabled");
	}
	// F3:
	// 使用しない
	toggleFn("fn3", "disabled");
	// F4:次ブロック
	// ヘッダ、ボディ以外は使用しない
	if (now_mode != '8' && able_check != 'true') {
		toggleFn("fn4", "disabled");
	}
	// F5:確定
	// テイル以外は使用しない
	if (able_commit != 'true') {
		toggleFn("fn5", "disabled");
	}
	// F6:前ページ
	// ページカウントが０、またはヘッダ部にいる場合は使用しない
	if (page_count == 0 || able_head_item == 'true') {
		toggleFn("fn6", "disabled");
	}
	// F7:次ページ
	// 新規・修正の場合、ヘッダ部・テイル部かつ次頁なしでは使用しない
	if (business_kbn=='1' || business_kbn=='2') {
		if (able_after_update == 'true' && hasMore == 'false') {
			toggleFn("fn7", "disabled");
		}
		if (able_head_item == 'true') {
			toggleFn("fn7", "disabled");
		}
	}
	// F7:次ページ
	// 削除・照会の場合、ヘッダ部または次ページなしの場合は使用しない
	if (business_kbn=='3' || business_kbn=='4') {
		if (able_head_item == 'true' || hasMore == 'false') {
			toggleFn("fn7", "disabled");
		}
	}
	// F8:戻る
	// 常に使用する
	//toggleFn("fn8", "disabled");
	// F9:最後
	// 新規・修正の場合、ヘッダ部・テイル部かつ次頁なしでは使用しない
	if (business_kbn=='3' || business_kbn=='4') {
		if (able_head_item == 'true' || hasMore == 'false') {
			toggleFn("fn9", "disabled");
		}
	}
	// F10・11:削除・挿入
	// ボディ部以外は使用しない
	if (able_body_item == 'false') {
		toggleFn("fn10", "disabled");
		toggleFn("fn11", "disabled");
	}
	// F12:印刷
	// テイル部以外は使用しない
	if (able_after_update != 'true') {
		toggleFn("fn12", "disabled");
	}

	// ファンクション動作設定
	function f1() {
		j$("#" + fid + "\\:findButton").trigger("click");
	}
	function f2() {
		displayDataSend();
		j$("#" + fid + "\\:prsKbnId2").trigger("click");
	}
	function f3() {}
	function f4() {
		if (now_mode == '8') {
			displayDataSend();
			nextBtnClick();
		}
		if (able_check == 'true') {
			displayDataSend();
			j$("#" + fid + "\\:checkButton").trigger("click");
		}
	}
	function f5() {
		displayDataSend();
		j$("#" + fid + "\\:torokuButton").trigger("click");
	}
	function f6() {
		displayDataSend();
  		j$("#" + fid + "\\:movePrev").trigger("click");
	}
	function f7() {
		displayDataSend();
		if (hasMore == 'false') {
	  		j$("#" + fid + "\\:makeLastPage").trigger("click");
		} else {
	  		j$("#" + fid + "\\:moveNext").trigger("click");
		}
	}
	function f8() {
		if (able_prev == 'true') {
			displayDataSend();
			j$("#" + fid + "\\:prevButton").trigger("click");
		} else {
			displayDataSend();
			j$("#" + fid + "\\:returnButton").trigger("click");
		}
	}
	function f9() {
		displayDataSend();
  		j$("#" + fid + "\\:moveLastPage").trigger("click");
	}
	function f10() {
		if (setColumNo()) {
			if (confirm("削除してよろしいですか？")) {
				displayDataSend();
				j$("#" + fid + "\\:deleteButton").trigger("click");
			} else {
				unblockAllFn();
			}
		} else {
			unblockAllFn();
		}
	}
	function f11() {
		if (setColumNo()) {
			displayDataSend();
			j$("#" + fid + "\\:insertButton").trigger("click");
		} else {
			unblockAllFn();
		}
	}
	function f12() {
		displayDataSend();
		j$("#" + fid + "\\:printButton").trigger("click");
	}
	
	function loopWait( timeWait )
	{
	    var timeStart = new Date().getTime();
	    var timeNow = new Date().getTime();
	    while( timeNow < (timeStart + timeWait ) )
	    {
	        timeNow = new Date().getTime();
	    }
	    return;
	}
--></script>
<!-- ########################### JavaSctipt設定 ここまで ########################### -->
</div><!-- end of wrap -->
</div><!-- end of ALL_DIV -->
</f:view>
</body>
</html>

