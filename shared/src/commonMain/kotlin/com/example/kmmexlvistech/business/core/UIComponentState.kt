package com.example.kmmexlvistech.business.core


sealed class UIComponentState {

   object Show: UIComponentState()

   object HalfShow: UIComponentState()

   object Hide: UIComponentState()
}
