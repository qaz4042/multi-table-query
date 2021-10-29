package com.lzb.mpmt.service;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperMainSub<MAIN> extends MultiWrapperWhere<MAIN, MultiWrapperMainSub<MAIN>> {
}
