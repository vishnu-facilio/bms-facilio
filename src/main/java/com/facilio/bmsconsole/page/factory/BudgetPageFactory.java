package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;

public class BudgetPageFactory extends PageFactory {

    public static Page getBudgetPage() {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget totalBudget = new PageWidget(PageWidget.WidgetType.TOTAL_BUDGET);
        totalBudget.addToLayoutParams(tab1Sec1, 8, 5);
        tab1Sec1.addWidget(totalBudget);

        PageWidget actualBudget = new PageWidget(PageWidget.WidgetType.ACTUAL_BUDGET_AMOUNT);
        actualBudget.addToLayoutParams(tab1Sec1, 8, 5);
        tab1Sec1.addWidget(actualBudget);

        PageWidget remainingBudget = new PageWidget(PageWidget.WidgetType.REMAINING_BUDGET);
        remainingBudget.addToLayoutParams(tab1Sec1, 8, 5);
        tab1Sec1.addWidget(remainingBudget);

        PageWidget budgetPrimaryDetails = new PageWidget(PageWidget.WidgetType.BUDGET_PRIMARY_DETAILS);
        budgetPrimaryDetails.addToLayoutParams(tab1Sec1, 24, 4);
        tab1Sec1.addWidget(budgetPrimaryDetails);

        PageWidget budgetSplitUp = new PageWidget(PageWidget.WidgetType.BUDGET_SPLIT_UP);
        budgetSplitUp.addToLayoutParams(tab1Sec1, 24, 14);
        tab1Sec1.addWidget(budgetSplitUp);

        PageWidget budgetNetIncome = new PageWidget(PageWidget.WidgetType.BUDGET_NET_INCOME);
        budgetNetIncome.addToLayoutParams(tab1Sec1, 24, 10);
        tab1Sec1.addWidget(budgetNetIncome);


        Page.Tab tab2 = page.new Tab("Notes & Attachments");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);
        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);


        return page;
    }

}
