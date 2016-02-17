/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.adempiere.pos;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.pos.search.POSQuery;
import org.adempiere.pos.search.QueryBPartner;
import org.adempiere.pos.search.QueryDocType;
import org.adempiere.pos.search.QueryOrderHistory;
import org.adempiere.pos.search.QueryProduct;
import org.adempiere.pos.service.I_POSPanel;
import org.adempiere.pos.service.I_POSQuery;
import org.adempiere.pos.service.POSQueryListener;
import org.compiere.apps.ADialog;
import org.compiere.model.I_M_Product;
import org.compiere.model.MWarehousePrice;
import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;


/**
 *	Customer Sub Panel
 *
 *         *Copyright � ConSerTi
 *  @author Comunidad de Desarrollo OpenXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � Jorg Janke
 *  @author Mario Calderon, mario.calderon@westfalia-it.com, Systemhaus Westfalia, http://www.westfalia-it.com
 *  @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 *  <li> Implement best practices
 *  @version $Id: SubOrder.java,v 1.1 2004/07/12 04:10:04 jjanke Exp $
 *  @version $Id: SubOrder.java,v 2.0 2015/09/01 00:00:00 mar_cal_westf
 *  @author victor.perez@e-evolution.com , http://www.e-evolution.com
 */
public class POSActionPanel extends POSSubPanel 
	implements ActionListener, I_POSPanel, POSQueryListener , POSLookupProductInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5895558315889871887L;

	/**
	 * 	Constructor
	 *	@param posPanel POS Panel
	 */
	public POSActionPanel (VPOS posPanel) {
		super (posPanel);
	}	//	PosSubCustomer


	/**	Buttons Command		*/
	private CButton 			buttonNew;
	private CButton 			buttonDocType;
	private CButton 			buttonBPartner;
	private CButton 			buttonProcess;
	private CButton 			buttonHistory;
	private CButton 			buttonBack;
	private CButton 			buttonNext;
	private CButton 			buttonCollect;
	private CButton 			buttonCancel;
	private CButton 			buttonLogout;
	/**	Button Panel		*/
	private CPanel 				buttonPanel;
	/**	For Show BPartner	*/
	private	POSTextField 		fieldProductName;
	/** Find Product Timer **/
	private javax.swing.Timer   findProductTimer;
	private POSLookupProduct 	lookupProduct;

	/**	Padding				*/
	private int 				topPadding;
	private int 				bottonPadding;
	private int 				rightPadding;
	private int 				leftPadding;

	/**	Process Action 						*/
	private POSActionMenu actionProcessMenu;


	/**	Logger			*/
	private static CLogger logger = CLogger.getCLogger(POSActionPanel.class);

	private final String ACTION_NEW         = "New";
	private final String ACTION_DOCTYPE     = "Assignment";
	private final String ACTION_BPARTNER    = "BPartner";
	private final String ACTION_PROCESS     = "Process";
	private final String ACTION_HISTORY     = "History";
	private final String ACTION_BACK       	= "Parent";
	private final String ACTION_NEXT  		= "Detail";
	private final String ACTION_PAYMENT     = "Payment";
	private final String ACTION_CANCEL      = "Cancel";
	private final String ACTION_LOGOUT      = "End";
	
	/**
	 * 	Initialize
	 */
	public void init() {
		//	Content
		setLayout(new GridBagLayout());
		//	Button Panel
		buttonPanel = new CPanel(new GridBagLayout());

		topPadding = 0;
		leftPadding = 1;
		bottonPadding = 0;
		rightPadding = 1;
		// NEW
		buttonNew = createButtonAction(ACTION_NEW, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		buttonNew.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonNew.setToolTipText("F2-" + Msg.translate(ctx, "new.order"));
		buttonPanel.add(buttonNew, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		// DOCTYPE
		buttonDocType = createButtonAction(ACTION_DOCTYPE,  KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		buttonDocType.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonDocType.setToolTipText("F10-" + Msg.translate(ctx, "C_DocType_ID"));
		buttonPanel.add(buttonDocType, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		// BPARTNER
		buttonBPartner = createButtonAction (ACTION_BPARTNER, KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK+Event.ALT_MASK));
		buttonBPartner.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonBPartner.setToolTipText("CTL+ALT+I-" + Msg.translate(ctx, "C_BPartner_ID"));
		buttonPanel.add(buttonBPartner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		// PROCESS
		buttonProcess = createButtonAction (ACTION_PROCESS, KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.ALT_MASK));
		buttonProcess.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonProcess.setToolTipText("ALT+P-" + Msg.translate(ctx, "Process"));
		buttonPanel.add(buttonProcess, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));

		// HISTORY
		buttonHistory = createButtonAction(ACTION_HISTORY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		buttonHistory.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonHistory.setToolTipText("F9-" + Msg.translate(ctx, "smenu.order.history"));
		buttonPanel.add(buttonHistory, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
 		// 	BACK
 		buttonBack = createButtonAction(ACTION_BACK, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.ALT_MASK));
 		buttonBack.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
 		buttonBack.setToolTipText("ALT-LEFT-" + Msg.translate(ctx, "prev"));
 		buttonPanel.add(buttonBack, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
 		buttonBack.setEnabled(true);
		
 		//	NEXT
 		buttonNext = createButtonAction(ACTION_NEXT, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,Event.ALT_MASK));
 		buttonNext.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
 		buttonNext.setToolTipText("ALT-RIGHT-" + Msg.translate(ctx, "next"));
 		buttonPanel.add(buttonNext, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		buttonNext.setEnabled(true);
 		
 		// PAYMENT
 		buttonCollect = createButtonAction(ACTION_PAYMENT, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
 		buttonCollect.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
 		buttonCollect.setToolTipText("F4-" + Msg.translate(ctx, "Payment"));
 		buttonPanel.add(buttonCollect, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		buttonCollect.setEnabled(false);
 		
 		// CANCEL
		buttonCancel = createButtonAction(ACTION_CANCEL, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		buttonCancel.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonCancel.setToolTipText("F3-" + Msg.translate(ctx, "POS.IsCancel"));
		buttonPanel.add(buttonCancel, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
 		
		// Logout
		buttonLogout = createButtonAction (ACTION_LOGOUT, KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.ALT_MASK));
		buttonLogout.setPreferredSize(new Dimension(posPanel.getButtonSize(), posPanel.getButtonSize()));
		buttonLogout.setToolTipText("ALT+L-" + Msg.translate(ctx, "LogOut"));
		buttonPanel.add(buttonLogout, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(topPadding, leftPadding, bottonPadding, rightPadding), 0, 0));
		// BP
		String labelName = Msg.translate(Env.getCtx(), I_M_Product.COLUMNNAME_M_Product_ID); 
		fieldProductName = new POSTextField(labelName, posPanel.getKeyboard());
		fieldProductName.setPlaceholder(labelName);
		fieldProductName.addActionListener(this);
		fieldProductName.setFont(posPanel.getFont());
		fieldProductName.setPreferredSize(new Dimension(250, posPanel.getFieldLenght()));
		fieldProductName.setMinimumSize(new Dimension(250, posPanel.getFieldLenght()));
		fieldProductName.setFocusable(true);

		//	Add Button Panel
		add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1
				,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		add(fieldProductName, new GridBagConstraints(0, 1, 1, 1, 1, 1
				,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

		if (posPanel.isEnableProductLookup() && !posPanel.isVirtualKeyboard()) {
			JComboBox<KeyNamePair> fillingComponent = new JComboBox<KeyNamePair>();
			Font font = new Font("monospaced", Font.PLAIN, 14);
			fillingComponent.setFont(font);

			lookupProduct = new POSLookupProduct(this, fieldProductName, 0);
			fieldProductName.addKeyListener(lookupProduct);
			fieldProductName.setFocusTraversalKeysEnabled(false);
			findProductTimer = new javax.swing.Timer(500, lookupProduct);
			lookupProduct.setFillingComponent(fillingComponent);
			lookupProduct.setPriceListVersionId(posPanel.getM_PriceList_Version_ID());
			lookupProduct.setWarehouseId(posPanel.getM_Warehouse_ID());
			findProductTimer.start();
			add(fillingComponent, new GridBagConstraints(0, 2, 1, 1, 1, 1
					, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 20), 0, 0));
		}

		actionProcessMenu = new POSActionMenu(posPanel);
		posPanel.listOrder();
		getMainFocus();
	}	//	init


	/**
	 * 	Dispose - Free Resources
	 */
	public void dispose()
	{
		fieldProductName = null;
		removeAll();
		super.dispose();
	}	//	dispose

	
	/**
	 * 	Distribute actions
	 *	@param actionEvent event
	 */
	public void actionPerformed (ActionEvent actionEvent) {
		String action = actionEvent.getActionCommand();
		if (action == null || action.length() == 0)
			return;
		logger.info( "PosSubCustomer - actionPerformed: " + action);
		try {
				if (actionEvent.getSource().equals(fieldProductName))
					return;
				//	New
				if (actionEvent.getSource().equals(buttonNew)) {
					posPanel.newOrder();
				} else if (actionEvent.getSource().equals(buttonDocType)) {
					if (posPanel.isUserPinValid()) {
						QueryDocType queryDocType = new QueryDocType(posPanel);
						queryDocType.addOptionListener(this);
						queryDocType.loadData();
						queryDocType.showView();
					}
				} else if (actionEvent.getSource().equals(buttonBPartner)) {
					if(posPanel.isDrafted() || posPanel.isInProgress())  {
						QueryBPartner queryBPartner = new QueryBPartner(posPanel);
						queryBPartner.addOptionListener(this);
						if(posPanel.isBPartnerStandard())
							queryBPartner.setResults(null);
						else
							queryBPartner.loadData();
						queryBPartner.showView();
					}
				} else if (actionEvent.getSource().equals(buttonProcess)){
					if (posPanel.isUserPinValid()) {
						actionProcessMenu.show(this, 340 , 60);
					}
					return;
				}
				else if (actionEvent.getSource().equals(buttonHistory)) {
					// For already created, but either not completed or not yet paid POS Orders
					POSQuery orderHistory = new QueryOrderHistory(posPanel);
					orderHistory.addOptionListener(this);
					orderHistory.showView();
					return;
				} else if (actionEvent.getSource().equals(buttonBack)){
					previousRecord();
				} else if (actionEvent.getSource().equals(buttonNext)){
					nextRecord();
				} else if (actionEvent.getSource().equals(buttonCollect)) {
					payOrder();
				} else if (actionEvent.getSource().equals(buttonCancel)) {
					if (posPanel.isUserPinValid())
						deleteOrder();
				} else if (actionEvent.getSource().equals(buttonLogout)) {	//	Logout
					posPanel.dispose();
					return;
				} 
				//	Refresh if not Payment, because Payment has its own logic
				if (!actionEvent.getSource().equals(buttonCollect))
					posPanel.refreshPanel();
		} catch (Exception exception) {
			ADialog.error(posPanel.getWindowNo(), this, exception.getLocalizedMessage());
		}
	}	//	actionPerformed

	public void getMainFocus() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getProductTimer().restart();
				fieldProductName.requestFocusInWindow();
			}
		});
	}

	/**************************************************************************
	 * 	Find/Set Product & Price
	 */
	public void findProduct(boolean isNewLine) throws Exception {
		getProductTimer().stop();
		String query = fieldProductName.getPlaceholder();
		if (query == null || query.length() == 0)
			return;
		query = query.toUpperCase();
		//	Test Number
		boolean allNumber = true;
		try {
			Integer.getInteger(query);
		} catch (Exception e) {
			allNumber = false;
		}
		String value = query;
		String name = query;
		String upc = (allNumber ? query : null);
		String sku = (allNumber ? query : null);
		//	
		MWarehousePrice[] results = MWarehousePrice.find (ctx,
				posPanel.getM_PriceList_Version_ID(), posPanel.getM_Warehouse_ID(),
				value, name, upc, sku, null);
		
		//	Set Result
		if (results.length == 1) {	//	one
			posPanel.addOrUpdateLine(results[0].getM_Product_ID(), Env.ONE);
			fieldProductName.setPlaceholder(results[0].getName());
		} else {	//	more than one
			posPanel.getFrame().getContentPane().invalidate();
			QueryProduct qt = new QueryProduct(posPanel);
			qt.addOptionListener(this);
			qt.setResults(results);
			qt.setQueryData(posPanel.getM_PriceList_Version_ID(), posPanel.getM_Warehouse_ID());
			qt.showView();
			fieldProductName.setValue(null);
			fieldProductName.setText("");
			fieldProductName.setPlaceholder("");
		}
		if (isNewLine) {
			posPanel.updateLineTable();
			if (posPanel.isNewLine())
				posPanel.setQuantity(BigDecimal.ONE);
			else
				posPanel.setQuantity(posPanel.getQty().add(BigDecimal.ONE));
			posPanel.updateLineTable();
			posPanel.refreshPanel();
			posPanel.changeViewPanel();
			posPanel.getMainFocus();
		}
	}	//	findProduct

	@Override
	public void quantityRequestFocus() {
		posPanel.quantityRequestFocus();
	}

	/**
	 * Previous Record Order
	 */
	public void previousRecord() {
		posPanel.previousRecord();
		//quantityPanel.changeViewPanel();
		//	Refresh
		posPanel.refreshPanel();
	}

	/**
	 * Next Record Order
	 */
	public void nextRecord() {
		posPanel.nextRecord();
		posPanel.refreshPanel();
	}
	
	/**
	 * Execute order payment
	 * If order is not processed, process it first.
	 * If it is successful, proceed to pay and print ticket
	 */
	private void payOrder() {
		//Check if order is completed, if so, print and open drawer, create an empty order and set cashGiven to zero
		if(!posPanel.hasOrder()) {
			ADialog.warn(posPanel.getWindowNo(), this,  Msg.getMsg(ctx, "POS.MustCreateOrder"));
		} else {
			posPanel.hideKeyboard();
			posPanel.showCollectPayment();
		}	
	}  // payOrder

	/**
	 * Execute deleting an order
	 * If the order is in drafted status -> ask to delete it
	 * If the order is in completed status -> ask to void it it
	 * Otherwise, it must be done outside this class.
	 */
	private void deleteOrder() {
		String errorMsg = null;
		String askMsg = "POS.DeleteOrder";
		if (posPanel.isCompleted()) {
			askMsg = "POS.OrderIsAlreadyCompleted";
		}
		//	Show Ask
		if (ADialog.ask(0, this, Msg.getMsg(ctx, Msg.getMsg(ctx, askMsg)))) {
			posPanel.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//	Cancel Order
			errorMsg = posPanel.cancelOrder();
			//	Set Cursor to default
			posPanel.getFrame().setCursor(Cursor.getDefaultCursor());
		}
		//	show if exists error
		if(errorMsg != null) {
			ADialog.error(posPanel.getWindowNo(),
					this, Msg.parseTranslation(ctx, errorMsg));
		}
		//	Update
		posPanel.refreshPanel();
	} // deleteOrder

	
	/**
	 * Is order fully pay ?
	 * Calculates if the given money is sufficient to pay the order
	 * 
	 */
	public boolean isOrderFullyPaid()
	{
		/*TODO
		BigDecimal given = new BigDecimal(f_cashGiven.getValue().toString());
		boolean paid = false;
		if (p_posPanel != null && p_posPanel.f_curLine != null)
		{
			MOrder order = p_posPanel.f_curLine.getOrder();
			BigDecimal total = new BigDecimal(0);
			if (order != null)
				total = order.getGrandTotal();
			paid = given.doubleValue() >= total.doubleValue();
		}
		return paid;
		*/
		return true;
	}	

	/**
	 * 	Open cash drawer
	 * 
	 */
//	public void openCashDrawer()
//	{
//		String port = "/dev/lp";
//		
//		byte data[] = new byte[] {0x1B, 0x40, 0x1C};
//		try {  
//            FileOutputStream m_out = null;
//			if (m_out == null) {
//                m_out = new FileOutputStream(port);  // No poner append = true.
//            }
//            m_out.write(data);
//        } catch (IOException e) {
//        }  
//	}

	@Override
	/*public void refreshPanel() {
		//	
	}*/

	public String validatePayment() {
		return null;
	}

	@Override
	public void changeViewPanel() {

	}

	@Override
	public void refreshPanel() {
		if(posPanel.hasOrder()) {
			if (lookupProduct != null && posPanel.isEnableProductLookup()) {
				lookupProduct.setPriceListVersionId(posPanel.getM_PriceList_Version_ID());
				lookupProduct.setWarehouseId(posPanel.getM_Warehouse_ID());
			}

			//	For Next
			buttonNext.setEnabled(!posPanel.isLastRecord() && posPanel.hasRecord());
			//	For Back
			buttonBack.setEnabled(!posPanel.isFirstRecord() && posPanel.hasRecord());
			//	For Collect
			if(posPanel.hasLines()
					&& !posPanel.isPaid()
					&& !posPanel.isVoided()
					&& !posPanel.isReturnMaterial()) {
				//	For Credit Order
				buttonCollect.setEnabled(true);
			} else {
				buttonCollect.setEnabled(false);
			}
			// For BusinessPartner and Document Type
			if(posPanel.isDrafted() || posPanel.isInProgress()) {
				buttonDocType.setEnabled(true);
				buttonBPartner.setEnabled(true);
			} else {
				buttonDocType.setEnabled(false);
				buttonBPartner.setEnabled(false);
			} 
			//	For Cancel Action
			buttonCancel.setEnabled(!posPanel.isVoided());
		} else {
			buttonNew.setEnabled(true);
			buttonHistory.setEnabled(true);
			//	For Next
			buttonNext.setEnabled(!posPanel.isLastRecord() && posPanel.hasRecord());
			//	For Back
			buttonBack.setEnabled(!posPanel.isFirstRecord() && posPanel.hasRecord());
			buttonCollect.setEnabled(false);
			//	For Cancel Action
			buttonCancel.setEnabled(false);
			// For BusinessPartner and Document Type
			buttonDocType.setEnabled(false);
			buttonBPartner.setEnabled(false);
		}
		//posPanel.changeViewPanel();
		buttonNew.setEnabled(true);
		buttonHistory.setEnabled(true);
		buttonProcess.setEnabled(true);
	}

	@Override
	public void okAction(I_POSQuery query) {
		try
		{
			if (query.getRecord_ID() <= 0)
				return;
			//	For Ticket
			if(query instanceof QueryOrderHistory) {
				posPanel.setOrder(query.getRecord_ID());
				posPanel.reloadIndex(query.getRecord_ID());
			} else if(query instanceof QueryBPartner) {
				if(!posPanel.hasOrder()) {
					posPanel.newOrder(query.getRecord_ID());
					posPanel.getMainFocus();
				} else {
					posPanel.configureBPartner(query.getRecord_ID());
				}
				//
				logger.fine("C_BPartner_ID=" + query.getRecord_ID());
			} else if(query instanceof QueryProduct) {
				if (query.getRecord_ID() > 0) {
					posPanel.addOrUpdateLine(query.getRecord_ID(), Env.ONE);
				}
			} else if(query instanceof QueryDocType) {
				if (query.getRecord_ID() > 0) {
					posPanel.setC_DocType_ID(query.getRecord_ID());
				}
			}
			//	Refresh
			posPanel.refreshPanel();
		}
		catch (AdempiereException exception) {
			ADialog.error(posPanel.getWindowNo(), this, exception.getLocalizedMessage());
		} catch (Exception exception) {
			ADialog.error(posPanel.getWindowNo(), this, exception.getLocalizedMessage());
		}

	}

	@Override
	public void cancelAction(I_POSQuery query) {
		//	Nothing
	}

	@Override
	public void moveUp() {
	}

	@Override
	public void moveDown() {
	}	

	public void resetPanel() {
		buttonNew.setEnabled(false);
		buttonHistory.setEnabled(false);
		buttonNext.setEnabled(false);
		buttonBack.setEnabled(false);
		buttonCollect.setEnabled(false);
		buttonCancel.setEnabled(false);
		buttonDocType.setEnabled(false);
		buttonBPartner.setEnabled(false);	
		buttonProcess.setEnabled(false);
	}

	public javax.swing.Timer getProductTimer()
	{
		return  findProductTimer;
	}
	
}//	POSActionPanel