package com.android.customeview

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期时间工具类
 * @author tao.pan
 */
object DateUtils {
    private val datetimeFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val dateFormat1 = SimpleDateFormat("yyyyMMdd")
    private val timeFormat = SimpleDateFormat("HH:mm:ss")
    private val noSeparatorFormat =
        SimpleDateFormat("yyyyMMddHHmmss")
    private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    fun stringToFormatDate(dateStr: String): String {
        return dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(
            6,
            8
        ) + " " + dateStr.substring(8, 10) + ":" + dateStr.substring(
            10,
            12
        ) + ":" + dateStr.substring(12, 14)
    }

    /**
     * 比较日期，判断交易日期与当前时间做比较
     * @param tradeDate 交易时间 格式：yyyyMMddHHmmss
     * @return 交易日期与当前日期相同返回true 其他情况返回false
     */
    private fun compareData(tradeDateTime: String?): Boolean {
        if (null == tradeDateTime || "" == tradeDateTime || tradeDateTime.length < 10) return false
        val tradeDateStr =
            stringToFormatDate(tradeDateTime).substring(0, 10)
        val tradeDate = parseDate(tradeDateStr)
        val currentDate =
            parseDate(currentDate())
        return if (currentDate.compareTo(tradeDate) == 0) {
            true
        } else {
            false
        }
    }

    /**
     * 获得当前日期时间
     *
     *
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     * @return
     */
    fun currentDatetime(): String {
        return datetimeFormat.format(now())
    }

    /**
     * 获得当前日期时间
     *
     *
     * 日期时间格式yyyyMMddHHmmss
     * @return
     */
    fun noSeparatorNowDate(): String {
        return noSeparatorFormat.format(now())
    }

    /**
     * HHmmss
     * @return
     */
    fun noSeparatorNowDateTime(): String {
        val str =
            noSeparatorFormat.format(now())
        return str.substring(str.length - 6, str.length)
    }

    /**
     * MMdd
     * @return
     */
    fun noSeparatorNowDateMonth(): String {
        val str =
            noSeparatorFormat.format(now())
        return str.substring(str.length - 10, str.length - 6)
    }

    /**
     * yyMMdd
     * @return
     */
    fun noSeparatorNowYearDateMonth(): String {
        val str =
            noSeparatorFormat.format(now())
        return str.substring(str.length - 12, str.length - 6)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val str = "000011|11111"
        println(str.split("\\|").toTypedArray()[0])
    }

    /**
     * 格式化日期时间
     *
     *
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     * @return
     */
    fun formatDateTime(date: Date?): String {
        return datetimeFormat.format(date)
    }

    /**
     * 格式化日期时间
     * @param date
     * @param pattern
     * 格式化模式，详见[SimpleDateFormat]构造器
     * `SimpleDateFormat(String pattern)`
     * @return
     */
    fun formatDatetime(date: Date?, pattern: String?): String {
        val customFormat =
            datetimeFormat.clone() as SimpleDateFormat
        customFormat.applyPattern(pattern)
        return customFormat.format(date)
    }

    /**
     * 获得当前日期
     *
     *
     * 日期格式yyyy-MM-dd
     * @return
     */
    fun currentDate(): String {
        return dateFormat.format(now())
    }

    /**
     * 格式化日期
     *
     *
     * 日期格式yyyy-MM-dd
     * @return
     */
    fun formatDate(date: Date?): String {
        return dateFormat.format(date)
    }

    /**
     * 格式化日期
     *
     *
     * 日期格式yyyyMMdd
     * @return
     */
    fun formatDate1(date: Date?): String {
        return dateFormat1.format(date)
    }

    /**
     * 格式化日期
     *
     *
     * 日期格式yyyyMMddHHmmss
     * @return
     */
    fun formatDateNoSeparator(date: Date?): String {
        return noSeparatorFormat.format(date)
    }

    /**
     * 获得当前时间
     *
     *
     * 时间格式HH:mm:ss
     * @return
     */
    fun currentTime(): String {
        return timeFormat.format(now())
    }

    /**
     * 格式化时间
     *
     *
     * 时间格式HH:mm:ss
     * @return
     */
    fun formatTime(date: Date?): String {
        return timeFormat.format(date)
    }

    /**
     * 获得当前时间的`java.util.Date`对象
     * @return
     */
    fun now(): Date {
        return Date()
    }

    fun calendar(): Calendar {
        val cal =
            GregorianCalendar.getInstance(Locale.CHINESE)
        cal.firstDayOfWeek = Calendar.MONDAY
        return cal
    }

    /**
     * 获得当前时间的毫秒数
     *
     *
     * 详见[System.currentTimeMillis]
     * @return
     */
    fun millis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 获得当前Chinese月份
     * @return
     */
    fun month(): Int {
        return calendar()[Calendar.MONTH] + 1
    }

    /**
     * 获得月份中的第几天
     * @return
     */
    fun dayOfMonth(): Int {
        return calendar()[Calendar.DAY_OF_MONTH]
    }

    /**
     * 今天是星期的第几天
     * @return
     */
    fun dayOfWeek(): Int {
        return calendar()[Calendar.DAY_OF_WEEK]
    }

    /**
     * 今天是年中的第几天
     * @return
     */
    fun dayOfYear(): Int {
        return calendar()[Calendar.DAY_OF_YEAR]
    }

    /**
     * 判断原日期是否在目标日期之前
     * @param src
     * @param dst
     * @return
     */
    fun isBefore(src: Date, dst: Date?): Boolean {
        return src.before(dst)
    }

    /**
     * 判断原日期是否在目标日期之后
     * @param src
     * @param dst
     * @return
     */
    fun isAfter(src: Date, dst: Date?): Boolean {
        return src.after(dst)
    }

    /**
     * 判断两日期是否相同
     * @param date1
     * @param date2
     * @return
     */
    fun isEqual(date1: Date, date2: Date?): Boolean {
        return date1.compareTo(date2) == 0
    }

    /**
     * 判断某个日期是否在某个日期范围
     * @param beginDate 日期范围开始
     * @param endDate 日期范围结束
     * @param src 需要判断的日期
     * @return
     */
    fun between(
        beginDate: Date,
        endDate: Date,
        src: Date?
    ): Boolean {
        return beginDate.before(src) && endDate.after(src)
    }

    /**
     * 获得当前月的最后一天
     * HH:mm:ss为0，毫秒为999
     * @return
     */
    fun lastDayOfMonth(): Date {
        val cal = calendar()
        cal[Calendar.DAY_OF_MONTH] = 0 // M月置零
        cal[Calendar.HOUR_OF_DAY] = 0 // H置零
        cal[Calendar.MINUTE] = 0 // m置零
        cal[Calendar.SECOND] = 0 // s置零
        cal[Calendar.MILLISECOND] = 0 // S置零
        cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1 // 月份+1
        cal[Calendar.MILLISECOND] = -1 // 毫秒-1
        return cal.time
    }

    /**
     * 获得当前月的第一天
     * HH:mm:ss SS为零
     * @return
     */
    fun firstDayOfMonth(): Date {
        val cal = calendar()
        cal[Calendar.DAY_OF_MONTH] = 1 // M月置1
        cal[Calendar.HOUR_OF_DAY] = 0 // H置零
        cal[Calendar.MINUTE] = 0 // m置零
        cal[Calendar.SECOND] = 0 // s置零
        cal[Calendar.MILLISECOND] = 0 // S置零
        return cal.time
    }

    private fun weekDay(week: Int): Date {
        val cal = calendar()
        cal[Calendar.DAY_OF_WEEK] = week
        return cal.time
    }

    /**
     * 获得周五日期
     * 注：日历工厂方法[.calendar]设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     * @return
     */
    fun friday(): Date {
        return weekDay(Calendar.FRIDAY)
    }

    /**
     * 获得周六日期
     * 注：日历工厂方法[.calendar]设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     * @return
     */
    fun saturday(): Date {
        return weekDay(Calendar.SATURDAY)
    }

    /**
     * 获得周日日期
     * 注：日历工厂方法[.calendar]设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     * @return
     */
    fun sunday(): Date {
        return weekDay(Calendar.SUNDAY)
    }

    /**
     * 将字符串日期时间转换成java.util.Date类型
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     * @param datetime
     * @return
     */
    fun parseDatetime(datetime: String?): Date {
        var date2 = Date()
        try {
            date2 = datetimeFormat.parse(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date2
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     * 日期时间格式yyyy-MM-dd
     * @param date
     * @return
     * @throws ParseException
     */
    fun parseDate(date: String?): Date {
        var date2 = Date()
        try {
            date2 = dateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date2
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     * 时间格式 HH:mm:ss
     * @param time
     * @return
     * @throws ParseException
     */
    fun parseTime(time: String?): Date {
        var date = Date()
        try {
            date = timeFormat.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    /**
     * 根据自定义pattern将字符串日期转换成java.util.Date类型
     * @param datetime
     * @param pattern
     * @return
     * @throws ParseException
     */
    fun parseDatetime(datetime: String?, pattern: String?): Date {
        var date2 = Date()
        val format =
            datetimeFormat.clone() as SimpleDateFormat
        format.applyPattern(pattern)
        try {
            date2 = format.parse(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date2
    }

    /**
     * Author: tao.pan
     * Function: 日期时间格式yyyy-MM-dd
     * @param date
     * @return
     * Modifications:
     * Modifier Administrator; Nov 14, 2012;
     * Create new Method
     * @throws ParseException
     */
    fun parseDate(date: Date?): Date {
        var date2 = Date()
        date2 = parseDate(
            dateFormat.format(date)
        )
        return date2
    }

    fun formatDate(times: Long): String {
        return datetimeFormat.format(Date(times))
    }

    fun add(date: Date?, calendarField: Int, amount: Int): Date {
        requireNotNull(date) { "The date must not be null" }
        val c = Calendar.getInstance()
        c.time = date
        c.add(calendarField, amount)
        return c.time
    }

    fun addDayToStr(date: Date?, day: Int): String {
        return formatDateNoSeparator(
            add(
                date,
                Calendar.DAY_OF_YEAR,
                day
            )
        )
    }

    fun addDayToFormatStr(date: Date?, day: Int): String {
        return formatDateTime(
            add(
                date,
                Calendar.DAY_OF_YEAR,
                day
            )
        )
    }

    fun addHourToStr(date: Date?, day: Int): String {
        return formatDateTime(
            add(
                date,
                Calendar.HOUR_OF_DAY,
                day
            )
        )
    }

    fun addDayToStr(day: Int): String {
        return formatDateTime(
            add(
                Date(),
                Calendar.DAY_OF_YEAR,
                day
            )
        )
    }

    fun addDay(day: Int): Date {
        return add(
            Date(),
            Calendar.DAY_OF_YEAR,
            day
        )
    }

    fun addDay(date: Date?, day: Int): Date {
        return add(date, Calendar.DAY_OF_YEAR, day)
    }

    fun toDate(source: String?): Date? {
        return toDateTime(
            source,
            DATE_FORMAT_PATTERN
        )
    }

    fun toDateTime(source: String?, pattern: String?): Date? {
        var date: Date? = null
        try {
            val dateFormat = SimpleDateFormat(pattern)
            date = dateFormat.parse(source)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    fun getYear(date: Date?): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.YEAR]
    }
}