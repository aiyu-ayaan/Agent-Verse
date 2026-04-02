package com.atech.api_integration_common.tuning

import com.atech.api_integration_common.model.AvModelConfig

interface AvModelTuning {
    fun applyTuning(modelConfig: AvModelConfig): AvModelConfig
}