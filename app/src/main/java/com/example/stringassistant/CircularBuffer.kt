package com.example.stringassistant

class CircularBuffer(private val size:Int) {
    private val bytes = ByteArray(size)
    private var readCursor=0
    private var writeCursor=0

    fun write(x:Byte){
        bytes[writeCursor]=x
        writeCursor=(writeCursor+1) % bytes.size
    }
    fun writeBlock(block:ByteArray){
        if(block.size > (bytes.size-writeCursor)){
            val wrapCount = block.size % (bytes.size - writeCursor)
            var blockCursor = 0
            for(i in 0 .. wrapCount) {
                val windowSize = (block.size - (bytes.size-writeCursor-1))
                if(blockCursor == windowSize){
                    this.write(block[block.size-1])
                    writeCursor=(writeCursor+1) % bytes.size
                } else {
                    block.copyInto(
                        bytes,
                        writeCursor,
                        blockCursor,
                        windowSize
                    )
                    writeCursor += ((windowSize - blockCursor) % bytes.size)
                    blockCursor += windowSize
                }

            }
        } else {
            block.copyInto(bytes)
            writeCursor = (writeCursor + block.size ) % bytes.size
        }
        readCursor = 0
    }

    fun read(): Byte?{
        val returnVal = bytes[readCursor]
        readCursor = (readCursor + 1) % bytes.size
        return returnVal
    }
}