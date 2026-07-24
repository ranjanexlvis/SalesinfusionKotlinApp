package com.example.kmmexlvistech.business.core

sealed class NetworkState{

   object Good: NetworkState()

   object Failed: NetworkState()

}
