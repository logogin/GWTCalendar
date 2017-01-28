package logogin.gwt.user.ui.client.widget.datepicker;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import logogin.gwt.logger.client.util.DateHelper;

/**
 * CalendarPanel
 * @author Pavel Danchenko
 * Jan 26, 2007
 *
 */
public class CalendarPanel extends PopupPanel implements MouseListener {

    private static final int TITLE_ROW = 0;
    private static final int WEEKDAYS_ROW = 1;
    private static final int DAYS_ROW = 2;
    private final Constants constants = (Constants)GWT.create(Constants.class);
   
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;
    
    private FlexTable m_days = new FlexTable(); 
    
    private Date m_selectedDate;
    private int m_shownYear;
    private int m_shownMonth;
    private Date[][] m_dateValues;
    
    private HasText m_widget;
    private Label monthTitle = new Label();
    private Label yearTitle = new Label();
    
    private Grid m_months = new Grid(2, 6);
    private Grid m_years = new Grid(12, 1);
    
    public CalendarPanel(boolean autoHide, HasText widget) {
        super(autoHide);
        setStyleName("DatePicker-CalendarPanel");
        m_widget = widget;
        m_selectedDate = new Date();
        FlowPanel panel = new FlowPanel();
        initDaysPanel();
        panel.add(m_days);
        
        initMonthsPanel();
        panel.add(m_months);
        
        initYearsPanel();
        panel.add(m_years);
        
        setWidget(panel);
        
        m_shownYear = getFullYear(m_selectedDate);
        m_shownMonth = m_selectedDate.getMonth();
        setMonth(m_shownMonth);
        setYear(m_shownYear);
        setDays();
        resetMonthsPanel();
        //sinkEvents(Event.MOUSEEVENTS);
        //DOM.setEventListener(getElement(), this);
    }
    
    private void initDaysPanel() {
        initTitle();
        initWeekdays();
        initDays();
        m_days.addTableListener(new TableListener() {
            public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
                onDayClicked(row, cell);
            }
        });
        //m_days.sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE);
        //DOM.setEventListener(m_days.getElement(), this);
    }
    
    private void initTitle() {
        m_days.getRowFormatter().setStyleName(TITLE_ROW,
                "DatePicker-CalendarPanel-Header");
        m_days.getFlexCellFormatter().setStyleName(TITLE_ROW, 0,
                "DatePicker-CalendarPanel-Header-Prev");
        Image prev = new Image("img/datepicker/prev.gif");
        prev.setStyleName("DatePicker-CalendarPanel-Header-Prev-icon");
        prev.addClickListener(new ClickListener() {
            public void onClick(Widget source) {
                setPrevMonth();
            }
        });
        m_days.setWidget(TITLE_ROW, 0, prev);
        
        m_days.getFlexCellFormatter().setColSpan(TITLE_ROW, 1, 5);
        m_days.getFlexCellFormatter().setStyleName(TITLE_ROW, 1,
                "DatePicker-CalendarPanel-Header-Title");
        FlowPanel title = new FlowPanel();

        monthTitle.setStyleName("DatePicker-CalendarPanel-Header-Title-Month");
        monthTitle.addMouseListener(this);
        title.add(monthTitle);

        yearTitle.setStyleName("DatePicker-CalendarPanel-Header-Title-Year");
        yearTitle.addMouseListener(this);
        title.add(yearTitle);
        m_days.setWidget(TITLE_ROW, 1, title);
        
        m_days.getFlexCellFormatter().setStyleName(TITLE_ROW, 2,
                "DatePicker-CalendarPanel-Header-Next");
        Image next = new Image("img/datepicker/next.gif");
        next.setStyleName("DatePicker-CalendarPanel-Header-Next-icon");
        next.addClickListener(new ClickListener() {
            public void onClick(Widget source) {
                setNextMonth();
            }
        });
        m_days.setWidget(TITLE_ROW, 2, next);
    }
    
    private void initWeekdays() {
        int i;
        for (i=0; i<7; i++) {
            m_days.getCellFormatter().setStyleName(WEEKDAYS_ROW, i,
                    "DatePicker-CalendarPanel-Weekday");
            m_days.setText(WEEKDAYS_ROW, i, constants.weekDays()[(constants.startWeekDay() + i)%7]);
        }
    }
    
    private void initDays() {
        m_days.setCellSpacing(0);
        m_days.setCellPadding(0);
        m_days.setStyleName("DatePicker-CalendarPanel-Days");
        int i;
        int j;
        for (i=0; i<6; i++) {
            for (j=0; j<7; j++) {
                m_days.getFlexCellFormatter().setStyleName(DAYS_ROW + i, j,
                        "DatePicker-CalendarPanel-Days-Day");
                Element td = m_days.getFlexCellFormatter().getElement(DAYS_ROW + i, j); 
                DOM.setEventListener(td, this);
                DOM.sinkEvents(td, Event.ONMOUSEOVER | Event.ONMOUSEOUT);
            }
        }
    }
    
    private void initMonthsPanel() {
        m_months.setCellPadding(0);
        m_months.setCellSpacing(0);
        m_months.setBorderWidth(0);
        m_months.setStyleName("DatePicker-CalendarPanel-Months");
        int i;
        int j;
        for (i=0; i<2; i++) {
            for (j=0; j<6; j++) {
                int month = getMonthIndex(i, j);
                m_months.setText(i, j, constants.monthsShort()[month]);
                Element td = m_months.getCellFormatter().getElement(i, j); 
                DOM.setEventListener(td, this);
                DOM.sinkEvents(td, Event.ONMOUSEOVER | Event.ONMOUSEOUT);
            }
        }
        m_months.addTableListener(new TableListener() {
            public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
                onMonthClicked(row, cell);
            }
        });
        m_months.setVisible(false);
    }
    
    private void resetMonthsPanel() {
        int i;
        int j;
        for (i=0; i<2; i++) {
            for (j=0; j<6; j++) {
                m_months.getCellFormatter().setStyleName(i, j,
                        "DatePicker-CalendarPanel-Months-Month");
            }
        }
    }
    
    private void onDayClicked(int row, int cell) {
        if ( DAYS_ROW <= row ) {
            hide();
            m_selectedDate = m_dateValues[row - DAYS_ROW][cell];
            m_widget.setText(DateHelper.dateToString(m_selectedDate));
        }
    }
    
    private void onMonthClicked(int row, int cell) {
        m_months.setVisible(false);
        setMonth(getMonthIndex(row, cell));
    }
    
    private int getMonthIndex(int row, int cell) {
        return m_months.getColumnCount()*row + cell;
    }
    
    private void setPrevMonth() {
        setMonth(m_shownMonth - 1);
    }
    
    private void setNextMonth() {
        setMonth(m_shownMonth + 1);
    }
    
    public void onMouseDown(Widget sender, int x, int y) {
    }

    public void onMouseEnter(Widget source) {
        if ( source == monthTitle ) {
            m_months.setVisible(true);
            m_years.setVisible(false);
            return;
        }
        if ( source == yearTitle ) {
            DOM.setStyleAttribute(m_years.getElement(), "left", m_days.getOffsetWidth() + "px");
            m_months.setVisible(false);
            m_years.setVisible(true);
            setYears(m_shownYear);
            return;
        }
    }

    public void onMouseLeave(Widget source) {
    }

    public void onMouseMove(Widget sender, int x, int y) {
    }

    public void onMouseUp(Widget sender, int x, int y) {
    }

    
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
    //            dragging = true;
    //            DOM.setCapture(m_days.getElement());
    //            dragStartX = x;
    //            dragStartY = y;
                break;
            case Event.ONMOUSEUP:
    //            dragging = false;
    //            DOM.releaseCapture(m_days.getElement());
                break;
            case Event.ONMOUSEMOVE:
    //            if (dragging) {
    //                int absX = x + m_days.getAbsoluteLeft();
    //                int absY = y + m_days.getAbsoluteTop();
    //                setPopupPosition(absX - dragStartX, absY - dragStartY);
    //            }
                break;
            case Event.ONMOUSEOVER: {
                Element target = DOM.eventGetTarget(event);
                String style = DOM.getAttribute(target, "className") + "-over";
                DOM.setAttribute(target, "className", style);
                break;
            }
            case Event.ONMOUSEOUT: {
                Element target = DOM.eventGetTarget(event);
                String style = DOM.getAttribute(target, "className");
                if ( style.endsWith("-over") ) {
                    style = style.substring(0, style.lastIndexOf("-over"));
                    DOM.setAttribute(target, "className", style);
                }
                break;
            }
        }
    }
    
    private void setDays() {
        m_dateValues = new Date[6][7];
        Date date = new Date(getShortYear(m_shownYear), m_shownMonth, 0);
        int prevMonthDays = date.getDay() + 1 - constants.startWeekDay() ;
        if ( 0 >= prevMonthDays ) {
            prevMonthDays+=7;
        }

        int prevMonthDate = date.getDate() - prevMonthDays;
        int i;
        for (i=0; i<prevMonthDays; i++) {
            int day = prevMonthDate + i + 1;
            m_days.setText(DAYS_ROW, i, String.valueOf(day));
            m_days.getFlexCellFormatter().setStyleName(DAYS_ROW, i,
                    "DatePicker-CalendarPanel-Days-Prev-month");
            m_dateValues[0][i] = new Date(date.getYear(), date.getMonth(), day);
        }
        date = new Date(getShortYear(m_shownYear), m_shownMonth + 1, 0);
        int monthDate = date.getDate();
        int row = 0;
        int cell = prevMonthDays;
        for (i=0; i<monthDate; i++) {
            if ( 0 == cell%7 ) {
                row++;
                cell = 0;
            }
            int day = i+1;
            m_days.setText(DAYS_ROW + row, cell, String.valueOf(day));
            m_dateValues[row][cell] = new Date(getShortYear(m_shownYear), m_shownMonth, day);
            if ( isSelectedDate(m_dateValues[row][cell]) ) {
                m_days.getFlexCellFormatter().setStyleName(DAYS_ROW + row, cell,
                        "DatePicker-CalendarPanel-Days-Day-selected");
            } else {
                m_days.getFlexCellFormatter().setStyleName(DAYS_ROW + row, cell,
                        "DatePicker-CalendarPanel-Days-Day");
            }
            cell++;
        }
        date = new Date(getShortYear(m_shownYear), m_shownMonth + 1, 1);
        for (i=0; i<(42 - (prevMonthDays + monthDate)); i++) {
            if ( 0 == cell%7 ) {
                row++;
                cell = 0;
            }
            int day = i+1;
            m_days.setText(DAYS_ROW + row, cell, String.valueOf(day));
            m_days.getFlexCellFormatter().setStyleName(DAYS_ROW + row, cell,
                    "DatePicker-CalendarPanel-Days-Next-month");
            m_dateValues[row][cell] = new Date(date.getYear(), date.getMonth(), day);
            cell++;
        }
    } 
    
    
    private void initYearsPanel() {
        m_years.setCellPadding(0);
        m_years.setCellSpacing(0);
        m_years.setBorderWidth(0);
        m_years.setStyleName("DatePicker-CalendarPanel-Years");
        Image up = new Image("img/datepicker/up.gif");
        m_years.setWidget(0, 0, up);
        Image down = new Image("img/datepicker/down.gif");
        m_years.setWidget(11, 0, down);
        m_years.addTableListener(new TableListener() {
            public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
                onYearClicked(row);
            }
        });
        int i;
        for (i=0; i<12; i++) {
            m_years.getCellFormatter().setStyleName(i, 0,
                    "DatePicker-CalendarPanel-Years-Year");
            Element td = m_years.getCellFormatter().getElement(i, 0); 
            DOM.setEventListener(td, this);
            DOM.sinkEvents(td, Event.ONMOUSEOVER | Event.ONMOUSEOUT);
        }
        DOM.setStyleAttribute(m_years.getElement(), "position", "absolute");
        DOM.setStyleAttribute(m_years.getElement(), "top", m_days.getAbsoluteTop() + "px");
        m_years.setVisible(false);
    }
    
    private void onYearClicked(int row) {
        int year = Integer.parseInt(m_years.getText(1, 0));
        if ( 0 == row ) {
            setYears(year + 10);
            return;
        }
        if ( 11 == row ) {
            setYears(year - 10);
            return;
        }
        m_years.setVisible(false);
        year = Integer.parseInt(m_years.getText(row, 0));
        setYear(year);
    }
    
    private void setYears(int fullYear) {
        int i;
        for (i=0; i<10; i++) {
            int year = fullYear - i;
            m_years.setText(i + 1, 0, String.valueOf(year));
            m_years.getCellFormatter().setStyleName(i + 1, 0, "DatePicker-CalendarPanel-Years-Year");
        }
    }
    
    private void setMonth(int month) {
        Date date = new Date(getShortYear(m_shownYear), month, 1);
        m_shownMonth = date.getMonth();
        m_shownYear = getFullYear(date);
        monthTitle.setText(constants.monthsFull()[m_shownMonth]);
        yearTitle.setText(String.valueOf(m_shownYear));
        setDays();
    }
    
    private void setYear(int year) {
        Date date = new Date(getShortYear(year), m_shownMonth, 1);
        m_shownMonth = date.getMonth();
        m_shownYear = getFullYear(date);
        monthTitle.setText(constants.monthsFull()[m_shownMonth]);
        yearTitle.setText(String.valueOf(m_shownYear));
        setDays();
    }
    
    private int getFullYear(Date date) {
        return date.getYear() + 1900;
    }

    private int getShortYear(int year) {
        return year - 1900;
    }
    
    private boolean isSelectedDate(Date date) {
        if ( date.getYear() == m_selectedDate.getYear()
                && date.getMonth() == m_selectedDate.getMonth()
                && date.getDate() == m_selectedDate.getDate() ) {
            return true;
        }
        return false;
    }
    
    public void setSelectedDate(Date date) {
        m_selectedDate = date;
        m_shownYear = getFullYear(m_selectedDate);
        m_shownMonth = m_selectedDate.getMonth();
        setMonth(m_shownMonth);
        setYear(m_shownYear);
        setDays();
        resetMonthsPanel();
    }
    
    public void show() {
        super.show();
    }
}
