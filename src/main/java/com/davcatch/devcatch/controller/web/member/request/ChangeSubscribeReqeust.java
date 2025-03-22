package com.davcatch.devcatch.controller.web.member.request;

import java.util.List;

import com.davcatch.devcatch.domain.tag.TagType;

import lombok.Data;

@Data
public class ChangeSubscribeReqeust {

	private boolean subscribeAll;

	private List<TagType> selectedTags;
}
