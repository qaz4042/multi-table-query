package com.bebetter.mtq.service.multiwrapper.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiTuple2<T1,T2>{
    private T1 t1;
    private T2 t2;
}
