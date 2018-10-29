package com.realizeitstudio.deteclife.utils;

import com.realizeitstudio.deteclife.category.CategoryListContract;
import com.realizeitstudio.deteclife.category.CategoryListPresenter;
import com.realizeitstudio.deteclife.object.ColorDefineTable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class ParseTimeTest {

    @Test
    @PrepareForTest({ParseTime.class})
    public void intToHourMin() {

        int intTestInput = 195;
        String strExpectResult = "3 hr 15 min";
        String strActualResult = ParseTime.intToHourMin(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 1;
        strExpectResult = "1 min";
        strActualResult = ParseTime.intToHourMin(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 59;
        strExpectResult = "59 min";
        strActualResult = ParseTime.intToHourMin(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 0;
        strExpectResult = "0 min";
        strActualResult = ParseTime.intToHourMin(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 3000;
        strExpectResult = "50 hr";
        strActualResult = ParseTime.intToHourMin(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);
    }


    @Test
    @PrepareForTest({ParseTime.class})
    public void intToHrM() {

        int intTestInput = 195;
        String strExpectResult = "3 h 15 m";
        String strActualResult = ParseTime.intToHrM(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 1;
        strExpectResult = "1 m";
        strActualResult = ParseTime.intToHrM(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 59;
        strExpectResult = "59 m";
        strActualResult = ParseTime.intToHrM(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 0;
        strExpectResult = "0 m";
        strActualResult = ParseTime.intToHrM(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);


        intTestInput = 3000;
        strExpectResult = "50 h";
        strActualResult = ParseTime.intToHrM(intTestInput);

        Assert.assertEquals(strExpectResult, strActualResult);
    }

//    @Test
//    public void intToHrM() {
//        String result = ParseTime.intToHrM(195);
//        assertEquals("3 h 15 m", result);
//    }




//    @Test
//    @PrepareForTest({ParseTime.class})
//    public void testStaticMethod() {
//        PowerMockito.mockStatic(ParseTime.class); //<-- PowerMock 靜態類
//        ParseTime parseTime = new ParseTime();
//
//        int intTestInput = 195;
//
//        String strExpectResult = "3 h 15 m";
//        String strActualResult = parseTime.intToHrM(intTestInput);
//
////        Mockito.when(ParseTime.intToHrM(intTestInput)).thenReturn(strExpectResult);
//
//        Assert.assertEquals(strExpectResult, strActualResult);
//    }

//
//    private ArrayList mockList;
//
//    @Before
//    public void setUp() throws Exception {
//        //MockitoAnnotations.initMocks(this);
//        //mock creation
//        mockList = mock(ArrayList.class);
//    }
//
//    @Test
//    public void sampleTest1() throws Exception {
//        //使用mock对象执行方法
//        mockList.add("one");
//        mockList.clear();
//
//        //检验方法是否调用
//        verify(mockList).add("one");
//        verify(mockList).clear();
//    }
//
//    @Test
//    public void sampleTest2() throws Exception {
//        //我们可以直接mock一个借口，即使我们并未声明它
//        ColorDefineTable colorDefineTable = mock(ColorDefineTable.class);
////        ParseTime parseTime = mock(ParseTime.class);
////        doReturn("3 h 15 m").when(parseTime).intToHrM(195);
//
//        when(colorDefineTable.getColorName()).thenReturn("getColorName"); //我们定义，当mockPresenter调用getUserName()时，返回qingmei2
//
//        String resultA = colorDefineTable.getColorName();
//        verify(colorDefineTable).getColorName();
//        Assert.assertEquals("getColorName", resultA); //断言 result = "3 h 15 m"
//
////        verify(parseTime).intToHrM(195); //校验 是否mockPresenter调用了getUserName()方法
////        Assert.assertEquals("3 h 15 m", resultA); //断言 result = "3 h 15 m"
//
////        verify(colorDefineTable).getUserDef();  //校验 是否mockPresenter调用了getPassword()方法
//        boolean resultB = colorDefineTable.getUserDef();  //因为未定义返回值，默认返回null
//        verify(colorDefineTable).getUserDef();
//        Assert.assertEquals(resultB, false);
//
////        when(colorDefineTable.getUserDef()).thenReturn(true); //我们定义，当mockPresenter调用getUserName()时，返回qingmei2
////        boolean resultBoolean = colorDefineTable.getUserDef();  //因为未定义返回值，默认返回null
////        verify(colorDefineTable).getUserDef();
////        Assert.assertEquals(resultBoolean, true);
//    }
//
//    @Test
//    public void sampleTest22() throws Exception {
//        //我们可以直接mock一个借口，即使我们并未声明它
//        ColorDefineTable colorDefineTable = mock(ColorDefineTable.class);
////        ParseTime parseTime = mock(ParseTime.class);
////        doReturn("3 h 15 m").when(parseTime).intToHrM(195);
//
////        when(colorDefineTable.getColorName()).thenReturn("getColorName"); //我们定义，当mockPresenter调用getUserName()时，返回qingmei2
////
////        String resultA = colorDefineTable.getColorName();
////        verify(colorDefineTable).getColorName();
////        Assert.assertEquals("getColorName", resultA); //断言 result = "3 h 15 m"
//
////        verify(parseTime).intToHrM(195); //校验 是否mockPresenter调用了getUserName()方法
////        Assert.assertEquals("3 h 15 m", resultA); //断言 result = "3 h 15 m"
//
////        verify(colorDefineTable).getUserDef();  //校验 是否mockPresenter调用了getPassword()方法
////        boolean resultB = colorDefineTable.getUserDef();  //因为未定义返回值，默认返回null
////        verify(colorDefineTable).getUserDef();
////        Assert.assertEquals(resultB, false);
//
//        //        verify(colorDefineTable).getUserDef();
//        when(colorDefineTable.getUserDef()).thenReturn(true); //我们定义，当mockPresenter调用getUserName()时，返回qingmei2
//        boolean resultBoolean = colorDefineTable.getUserDef();  //因为未定义返回值，默认返回null
//        Assert.assertEquals(resultBoolean, true);
//    }

}