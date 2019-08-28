package kr.ac.snu.hcil.datahalo.utils

class ANHComponentUIDGenerator {
    //App Notification Halo 내에서 unique한 ID 부여 체계
    //TODO(ghhan)뭔가 이상한데, Halo별로 따로 돌아가야하는 것 같은데? 겹칠 가능성은 적은데 다른 데 한 바퀴 돌고 오면 문제 생길수도있?
    companion object {
        const val GLOBAL_PIVOT = 1
        private val localPivotRange = Pair(10, 99)
        private val visObjectRange = Pair(100, 999)

        private var nextLocalPivotID: Int = localPivotRange.first
        private var nextVisObjectID:Int = visObjectRange.first

        fun generateVisObjectUID():Int {
            return if(nextVisObjectID == visObjectRange.second){
                nextVisObjectID = visObjectRange.first
                visObjectRange.second
            } else {
                nextVisObjectID += 1
                (nextVisObjectID - 1)
            }
        }

        fun generateLocalPivotUID():Int{
            return if(nextLocalPivotID == localPivotRange.second){
                nextLocalPivotID = localPivotRange.first
                localPivotRange.second
            } else {
                nextLocalPivotID += 1
                (nextLocalPivotID - 1)
            }
        }
    }
}