package com.davcatch.devcatch.web.controller.member.request;

import java.util.List;

import com.davcatch.devcatch.domain.tag.TagType;

import lombok.Data;

@Data
public class ChangeSubscribeReqeust {

	private boolean subscribeAll;

	private List<TagType> selectedTags;
}
