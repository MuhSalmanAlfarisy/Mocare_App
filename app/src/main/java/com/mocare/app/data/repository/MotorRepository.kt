package com.mocare.app.data.repository

import com.mocare.app.data.local.dao.MotorDao
import com.mocare.app.data.local.entity.MotorEntity
import kotlinx.coroutines.flow.Flow

class MotorRepository(private val motorDao: MotorDao) {
    val allMotors: Flow<List<MotorEntity>> = motorDao.getAllMotors()

    fun getMotorById(id: Long): Flow<MotorEntity?> = motorDao.getMotorById(id)

    suspend fun insert(motor: MotorEntity): Long = motorDao.insertMotor(motor)
    suspend fun update(motor: MotorEntity) = motorDao.updateMotor(motor)
    suspend fun delete(motor: MotorEntity) = motorDao.deleteMotor(motor)
}
