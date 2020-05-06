package com.ckh.org.rssnews.vo

data class Data(val title:String, val des:String, val link: String, val img:String, val tag1:String, val tag2:String, val tag3:String ){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (title != other.title) return false
        if (des != other.des) return false
        if (link != other.link) return false
        if (img != other.img) return false
        if (tag1 != other.tag1) return false
        if (tag2 != other.tag2) return false
        if (tag3 != other.tag3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + des.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + img.hashCode()
        result = 31 * result + tag1.hashCode()
        result = 31 * result + tag2.hashCode()
        result = 31 * result + tag3.hashCode()
        return result
    }
}