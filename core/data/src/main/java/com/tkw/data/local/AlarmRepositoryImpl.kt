package com.tkw.data.local

import com.tkw.database.AlarmDao
import com.tkw.domain.AlarmRepository
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.AlarmSettings
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmManager: IAlarmManager
): AlarmRepository {
    override fun update(setting: AlarmSettings) {

    }

    override fun wakeAllAlarm() {
        //모든 알람 가져와서 실행. 알람 객체는 현재 enable 상태 가지고 있고, enable상태인 알람만 전부 다시 켜기.
    }

    override fun setAlarm(startTime: Long, alarmId: Int) {
        val interval = -1   //dao에서 현재 알람세팅 가져와서 해당 인터벌로 세팅.
        // -1인 경우 인터벌 없이 24시간 후에 울리도록 알람매니저에서 세팅함.
        if(alarmId != -1) {
            alarmManager.setAlarm(startTime, interval, alarmId)
        }
        //dao에도 알람 추가 또는 수정
    }

    override fun getAlarm(alarmId: Int) {

    }

    override fun getAllAlarm() {
        //모든 알람 객체 가져오기
    }

    override fun cancelAlarm() {
        alarmManager.cancelAlarm()
    }

    override fun cancelAllAlarm() {
        //모든 알람 취소
    }
}