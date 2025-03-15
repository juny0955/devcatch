package com.davcatch.devcatch.controller.member;

import java.util.List;

import com.davcatch.devcatch.domain.TagType;

import lombok.Data;

@Data
public class ChangeSubscribeReqeust {

	private boolean subscribeAll;

	private List<TagType> selectedTags;
}
