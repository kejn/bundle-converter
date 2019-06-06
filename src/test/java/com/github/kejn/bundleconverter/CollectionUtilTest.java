package com.github.kejn.bundleconverter;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for @{@link CollectionUtil} class.
 *
 * @author kejn
 */
public class CollectionUtilTest {

    @Test
    public void returnDefaultElementIfOutOfBounds() {
        // given
        List<String> list = new ArrayList<>();
        String[] array = new String[0];

        // then
        assertEquals("", CollectionUtil.getOrDefault(null,0,""));
        assertEquals("", CollectionUtil.getOrDefault(list,0,""));
        assertEquals("", CollectionUtil.getOrDefault(list,-1,""));

        assertEquals("", CollectionUtil.getOrDefaultFromArray(null,0,""));
        assertEquals("", CollectionUtil.getOrDefaultFromArray(array,-1,""));
        assertEquals("", CollectionUtil.getOrDefaultFromArray(array,-1,""));
    }

}
