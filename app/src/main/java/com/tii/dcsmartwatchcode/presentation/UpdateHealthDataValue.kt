package com.tii.dcsmartwatchcode.presentation

interface UpdateHealthDataValue {

	fun valueUpdated() : HealthDataClass
	fun error()
}